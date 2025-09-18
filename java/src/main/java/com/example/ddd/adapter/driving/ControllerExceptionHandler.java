package com.example.ddd.adapter.driving;

import com.example.ddd.domain.NotLoginException;
import com.example.ddd.domain.ValidationException;
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
     * NotLoginException异常处理器
     */
    @ExceptionHandler(value = NotLoginException.class)
    @ResponseBody
    public Result notLoginExceptionHandler(HttpServletResponse response, NotLoginException e) {
        return Result.error(ResultCodes.ERROR_NOT_LOGIN, e.getMessage());
    }

    /**
     * ValidationException异常处理器
     */
    @ExceptionHandler(value = ValidationException.class)
    @ResponseBody
    public Result validationExceptionHandler(HttpServletResponse response, ValidationException e) {
        return Result.error(ResultCodes.ERROR_VALIDATION, e.getMessage());
    }

    /**
     * 默认异常处理器
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public Result defaultExceptionHandler(HttpServletResponse response, Exception e) {
        logger.error("捕捉到未处理的异常: {}", e.getMessage());

        // 404异常设置状态码
        if(e instanceof NoResourceFoundException) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }

        //String message = e.getMessage();
        String message = e.toString();
        if(message == null) {
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            message = errors.toString();
        }
        return Result.error(ResultCodes.ERROR_DEFAULT, message);
    }
}