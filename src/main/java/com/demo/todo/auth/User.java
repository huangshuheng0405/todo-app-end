package com.demo.todo.auth;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * 用户表。对应安卓端 Session 里的 userId / email / displayName。
 */
@Entity
@Table(name = "users")
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 登录账号,全库唯一;注册时会先 trim + 转小写再落库 */
    @Column(nullable = false, unique = true)
    private String email;

    /** 展示名。目前直接用邮箱 @ 前缀,和安卓端 LocalAuthRepository 的行为一致 */
    @Column(nullable = false)
    private String displayName;

    /** BCrypt 哈希后的密码,绝不存明文 */
    @Column(nullable = false)
    private String passwordHash;

    /** 注册时间,epoch 毫秒 */
    @Column(nullable = false)
    private Long createdAt;
}
