package com.demo.todo.auth;

/**
 * 登录 / 注册成功的响应,字段与安卓端 Session 数据类一一对应。
 * userId 用 String 是为了和安卓端 Session.userId(String) 对齐。
 */
public record SessionResponse(
        String userId,
        String email,
        String displayName,
        String token
) {
}
