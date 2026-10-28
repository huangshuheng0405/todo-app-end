package com.demo.todo.auth;

import jakarta.validation.constraints.NotBlank;

/**
 * 注册 / 登录共用的请求体。格式和安卓端注释里约定的一致:{ email, password }。
 */
public record RegisterRequest(
        @NotBlank(message = "邮箱不能为空") String email,
        @NotBlank(message = "密码不能为空") String password
) {
}
