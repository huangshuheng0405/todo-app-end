package com.demo.todo.common;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

/**
 * 全局异常处理：所有 Controller 抛出的异常都在这里变成统一格式的 JSON。
 * 安卓端只需要按 { error, message } 解析即可。
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** 业务异常:用异常里带的状态码和错误码 */
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<Map<String, String>> handleApi(ApiException ex) {
        return ResponseEntity.status(ex.getStatus())
                .body(Map.of("error", ex.getCode(), "message", ex.getMessage()));
    }

    /** @Valid 校验失败(比如 title 为空):固定 400 */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(e -> e.getField() + " " + e.getDefaultMessage())
                .orElse("参数不合法");
        return ResponseEntity.badRequest()
                .body(Map.of("error", "VALIDATION_FAILED", "message", message));
    }

    /** 兜底:没预料到的异常一律 500,避免把堆栈直接暴露给客户端 */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleOther(Exception ex) {
        ex.printStackTrace(); // 学习阶段先打到控制台,正式项目应换成日志框架
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "INTERNAL_ERROR", "message", "服务器内部错误"));
    }
}
