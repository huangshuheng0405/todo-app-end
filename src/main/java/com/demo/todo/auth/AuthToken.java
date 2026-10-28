package com.demo.todo.auth;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * 登录 token 表。登录成功时生成一行,请求时通过 Authorization: Bearer <token> 找回用户。
 * 退出登录即删除对应行;过期 token 会在登录时被顺带清理。
 */
@Entity
@Table(name = "auth_tokens", indexes = @Index(name = "idx_tokens_user", columnList = "userId"))
@Getter
@Setter
public class AuthToken {

    /** 随机字符串,本身就是主键 */
    @Id
    @Column(length = 64)
    private String token;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long createdAt;

    /** epoch 毫秒,超过即视为未登录 */
    @Column(nullable = false)
    private Long expiresAt;
}
