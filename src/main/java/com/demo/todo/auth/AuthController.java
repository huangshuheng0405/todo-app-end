package com.demo.todo.auth;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * 认证接口,与安卓端 AuthRepository 注释里约定的路径一致:
 * - POST /auth/register  注册
 * - POST /auth/login     登录
 * - POST /auth/logout    退出(需要带 token)
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public SessionResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public SessionResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    /** token 由拦截器校验后放进 request attribute,这里直接拿来删 */
    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(@RequestAttribute("token") String token) {
        authService.logout(token);
    }
}
