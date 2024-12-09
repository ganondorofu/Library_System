package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByUsernameAndIsLocked(String username, boolean isLocked);

    Optional<User> findByUsernameAndFailedLoginAttemptsGreaterThanEqual(String username, int attempts);

    // mfaSecret を更新するクエリメソッドを追加
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.mfaSecret = :secret WHERE u.username = :username")
    void updateMfaSecret(@Param("username") String username, @Param("secret") String secret);
    
    // ユーザーのロールを検索（必要に応じてカスタムクエリを追加）
    Optional<String> findRoleByUsername(String username);
}
