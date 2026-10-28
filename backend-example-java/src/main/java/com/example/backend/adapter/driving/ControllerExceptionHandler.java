package com.example.backend.adapter.driving;

import com.example.backend.adapter.driven.GatewayException;
import com.example.backend.adapter.driven.PersistenceException;
import com.example.backend.application.ApplicationConfig;
import com.example.backend.application.ApplicationException;
import com.example.backend.application.lock.LockException;
import com.example.backend.domain.DomainException;
import com.example.backend.utility.UtilityException;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * 全局异常处理器
 */
@ControllerAdvice
public class ControllerExceptionHandler {

    protected static final Logger logger = LoggerFactory.getLogger(ControllerExceptionHandler.class);

    private final ApplicationConfig applicationConfig;

    public ControllerExceptionHandler(ApplicationConfig applicationConfig) {
        this.applicationConfig = applicationConfig;
    }

    /**
     * NotLoginException 异常处理器
     */
    @ExceptionHandler(value = NotLoginException.class)
    @ResponseBody
    public Result notLoginExceptionHandler(NotLoginException ex) {
        return Result.error(ResultCodes.ERROR_NOT_LOGIN, "未登录");
    }

    /**
     * LockException 异常处理器
     */
    @ExceptionHandler(value = LockException.class)
    @ResponseBody
    public Result lockExceptionHandler(LockException ex) {
        logger.error("捕捉到 LockException 异常: ", ex);
        return Result.error(ResultCodes.ERROR_DEFAULT, "系统繁忙，请稍后再试");
    }

    /**
     * NoResourceFoundException 异常处理器
     */
    @ExceptionHandler(value = NoResourceFoundException.class)
    public ResponseEntity<Void> noResourceFoundExceptionHandler(NoResourceFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    /**
     * 兜底异常处理器
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public Result exceptionHandler(HttpServletResponse response, Exception ex) {
        String message = ex.getMessage();        // 仅异常信息
        // String message = ex.toString();       // 异常类名: 异常信息

        // 从堆栈中获取异常信息
        // if (message == null) {
        //    StringWriter errors = new StringWriter();
        //    e.printStackTrace(new PrintWriter(errors));
        //    message = errors.toString();
        // }

        if (ex instanceof UtilityException
                || ex instanceof DomainException
                || ex instanceof ApplicationException
                || ex instanceof PersistenceException
                || ex instanceof GatewayException
                || ex instanceof ControllerException
        ) {
            // 自定义异常返回异常信息
            // response.setStatus(HttpServletResponse.SC_OK);
            return Result.error(ResultCodes.ERROR_DEFAULT, message);
        } else {
            // 非自定义异常记录日志
            logger.error("捕捉到非自定义异常", ex);

            // response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            if (applicationConfig.isAppEnvDev()) {
                // 开发环境非自定义异常显示具体信息
                return Result.error(ResultCodes.ERROR_DEFAULT, message);
            } else {
                // 生产环境非自定义异常显示统一出错信息
                return Result.error(ResultCodes.ERROR_DEFAULT, "系统发生错误");
            }
        }
    }
}
