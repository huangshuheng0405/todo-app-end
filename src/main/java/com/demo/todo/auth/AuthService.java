package com.demo.todo.auth;

import com.demo.todo.common.ApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.regex.Pattern;

/**
 * 注册 / 登录 / token 签发。
 * 校验规则与安卓端 AuthValidation 保持一致:邮箱同一套正则,密码最少 6 位。
 */
@Service
public class AuthService {

    private static final Pattern EMAIL_REGEX =
            Pattern.compile("^[A-Za-z0-9._%+\\-]+@[A-Za-z0-9.\\-]+\\.[A-Za-z]{2,}$");
    private static final int MIN_PASSWORD_LENGTH = 6;

    private final UserRepository userRepository;
    private final AuthTokenRepository tokenRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final long tokenTtlMillis;

    public AuthService(UserRepository userRepository,
                       AuthTokenRepository tokenRepository,
                       BCryptPasswordEncoder passwordEncoder,
                       @Value("${app.token-ttl-days:30}") long tokenTtlDays) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenTtlMillis = tokenTtlDays * 24 * 60 * 60 * 1000L;
    }

    @Transactional
    public SessionResponse register(RegisterRequest request) {
        String email = normalizeEmail(request.email());
        validateEmail(email);
        validatePassword(request.password());

        if (userRepository.existsByEmail(email)) {
            throw ApiException.conflict("EMAIL_TAKEN", "该邮箱已注册");
        }

        User user = new User();
        user.setEmail(email);
        user.setDisplayName(email.substring(0, email.indexOf('@')));
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setCreatedAt(System.currentTimeMillis());
        userRepository.save(user);

        return issueSession(user);
    }

    @Transactional
    public SessionResponse login(LoginRequest request) {
        String email = normalizeEmail(request.email());
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> ApiException.unauthorized("BAD_CREDENTIALS", "邮箱或密码错误"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw ApiException.unauthorized("BAD_CREDENTIALS", "邮箱或密码错误");
        }

        return issueSession(user);
    }

    /** 退出登录:删掉这条 token,之后再用就是 401 */
    @Transactional
    public void logout(String token) {
        tokenRepository.deleteById(token);
    }

    /** 供拦截器使用:token 有效且未过期则返回用户 id,否则 null */
    public Long resolveUserId(String token) {
        return tokenRepository.findById(token)
                .filter(t -> t.getExpiresAt() > System.currentTimeMillis())
                .map(AuthToken::getUserId)
                .orElse(null);
    }

    private SessionResponse issueSession(User user) {
        // 顺手清掉这个用户的过期 token,表不会无限膨胀
        tokenRepository.deleteExpiredByUserId(user.getId(), System.currentTimeMillis());

        AuthToken token = new AuthToken();
        token.setToken(UUID.randomUUID().toString().replace("-", ""));
        token.setUserId(user.getId());
        token.setCreatedAt(System.currentTimeMillis());
        token.setExpiresAt(token.getCreatedAt() + tokenTtlMillis);
        tokenRepository.save(token);

        return new SessionResponse(
                String.valueOf(user.getId()),
                user.getEmail(),
                user.getDisplayName(),
                token.getToken()
        );
    }

    private String normalizeEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase();
    }

    private void validateEmail(String email) {
        if (!EMAIL_REGEX.matcher(email).matches()) {
            throw ApiException.badRequest("EMAIL_INVALID", "邮箱格式不正确");
        }
    }

    private void validatePassword(String password) {
        if (password == null || password.length() < MIN_PASSWORD_LENGTH) {
            throw ApiException.badRequest("PASSWORD_TOO_SHORT", "密码至少 6 位");
        }
    }
}
