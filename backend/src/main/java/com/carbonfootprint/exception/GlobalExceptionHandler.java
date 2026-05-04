package com.carbonfootprint.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleAllExceptions(Exception e, HttpServletRequest request) {
        System.err.println("\n================ 🚨 捕获到全局致命异常 ================");
        System.err.println("请求路径: " + request.getRequestURI());
        System.err.println("异常类型: " + e.getClass().getName());
        System.err.println("异常信息: " + e.getMessage());
        e.printStackTrace();
        System.err.println("====================================================\n");

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("服务器内部爆炸, 错误信息: " + e.getMessage());
    }
}