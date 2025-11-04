package com.example.backend.adapter.driving;

import com.example.backend.application.lock.LockException;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * 全局异常处理器
 */
@ControllerAdvice
public class ControllerExceptionHandler {

    protected static final Logger logger = LoggerFactory.getLogger(ControllerExceptionHandler.class);

    /**
     * NotLoginException 异常处理器
     */
    @ExceptionHandler(value = NotLoginException.class)
    @ResponseBody
    public Result notLoginExceptionHandler(HttpServletResponse response, NotLoginException ex) {
        return Result.error(ResultCodes.ERROR_NOT_LOGIN, ex.getMessage());
    }

    /**
     * LockException 异常处理器
     */
    @ExceptionHandler(value = LockException.class)
    @ResponseBody
    public Result lockExceptionHandler(HttpServletResponse response, LockException ex) {
        logger.error("捕捉到 LockException 异常: ", ex);
        return Result.error(ResultCodes.ERROR_DEFAULT, "系统繁忙，请稍后再试");
    }

    /**
     * 默认异常处理器
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public Result defaultExceptionHandler(HttpServletResponse response, Exception ex) {
        logger.error("捕捉到未处理的异常", ex);

        // NoResourceFoundException 设置状态码 404
        if (ex instanceof NoResourceFoundException)
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        //else
        //    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

        String message = ex.getMessage();        // 仅异常信息
        // String message = ex.toString();       // 异常类名: 异常信息
        //if (message == null) {
        //    StringWriter errors = new StringWriter();
        //    e.printStackTrace(new PrintWriter(errors));
        //    message = errors.toString();
        //}
        return Result.error(ResultCodes.ERROR_DEFAULT, message);
    }
}