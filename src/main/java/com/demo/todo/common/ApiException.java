package com.demo.todo.common;

import org.springframework.http.HttpStatus;

/**
 * 业务异常。抛出后由 {@link GlobalExceptionHandler} 统一转成
 * { "error": "错误码", "message": "给人看的描述" } 的 JSON 响应。
 *
 * 安卓端拿到 HTTP 状态码 + error 码就能映射到对应的提示文案。
 */
public class ApiException extends RuntimeException {

    private final HttpStatus status;
    private final String code;

    public ApiException(HttpStatus status, String code, String message) {
        super(message);
        this.status = status;
        this.code = code;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getCode() {
        return code;
    }

    // ---- 常用快捷写法 ----

    public static ApiException badRequest(String code, String message) {
        return new ApiException(HttpStatus.BAD_REQUEST, code, message);
    }

    public static ApiException unauthorized(String code, String message) {
        return new ApiException(HttpStatus.UNAUTHORIZED, code, message);
    }

    public static ApiException notFound(String code, String message) {
        return new ApiException(HttpStatus.NOT_FOUND, code, message);
    }

    public static ApiException conflict(String code, String message) {
        return new ApiException(HttpStatus.CONFLICT, code, message);
    }
}
