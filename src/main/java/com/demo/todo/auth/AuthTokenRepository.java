package com.demo.todo.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface AuthTokenRepository extends JpaRepository<AuthToken, String> {

    /** 清理某个用户的过期 token,登录时顺手调用 */
    @Modifying
    @Query("DELETE FROM AuthToken t WHERE t.userId = :userId AND t.expiresAt < :now")
    int deleteExpiredByUserId(Long userId, long now);
}
