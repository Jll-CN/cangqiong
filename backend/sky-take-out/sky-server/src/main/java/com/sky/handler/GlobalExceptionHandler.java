package com.sky.handler;

import com.sky.constant.MessageConstant;
import com.sky.exception.BaseException;
import com.sky.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理器，处理项目中抛出的业务异常，捕获之后发送到前端页面
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 捕获业务异常
     * @param ex
     * @return
     */
    @ExceptionHandler
    public Result exceptionHandler(BaseException ex){
        log.error("异常信息：{}", ex.getMessage());
        return Result.error(ex.getMessage());
    }

    /**
     * 捕获 SQL 异常, 未来可能有其他类型的 SQL 异常
     */
    @ExceptionHandler
    public Result exceptionHandler(SQLIntegrityConstraintViolationException ex){

        // getMessage --> Duplicate entry 'zhangsan' for key 'employee.idx_username'
        String message = ex.getMessage();
        log.error("异常信息, {}", message);

        // 拆解错误信息，拼接字符串，提供给前端。
        if (message.contains("Duplicate entry")) {
            String[] split = message.split(" ");
            String username = split[2];
            // String msg = username + "账户已经存在";       // 避免硬编码
            String msg = username + MessageConstant.ALREADY_EXIST;  // MessageConstant 中存放消息的常量。

            // 拼接字符串错误信息
            return Result.error(msg);
        } else {
            return Result.error(MessageConstant.UNKNOWN_ERROR);     // 如果字符串中不含有 Duplicate Entry， 就返回未知错误。
        }
    }

}
