package com.demo.todo.common;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 只引入 spring-security-crypto 这一个模块,用 BCrypt 给密码做哈希。
 * 不引入完整的 Spring Security,避免默认的登录拦截干扰我们自己的 token 认证。
 */
@Configuration
public class AppConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
