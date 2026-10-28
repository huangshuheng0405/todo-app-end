package com.demo.todo.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

/**
 * 认证拦截器:除注册/登录外的所有请求都要带
 *   Authorization: Bearer <token>
 * 校验通过后把用户 id 放进 request attribute,Controller 里用
 * @RequestAttribute("uid") Long uid 就能拿到当前用户。
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    private final AuthService authService;

    public AuthInterceptor(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws IOException {
        // CORS 预检请求不带自定义头,直接放行
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            writeUnauthorized(response, "MISSING_TOKEN", "缺少登录凭证");
            return false;
        }

        String token = header.substring(7).trim();
        Long userId = authService.resolveUserId(token);
        if (userId == null) {
            writeUnauthorized(response, "INVALID_TOKEN", "登录已过期,请重新登录");
            return false;
        }

        request.setAttribute("uid", userId);
        request.setAttribute("token", token);
        return true;
    }

    private void writeUnauthorized(HttpServletResponse response, String code, String message)
            throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(
                "{\"error\":\"" + code + "\",\"message\":\"" + message + "\"}");
    }
}
