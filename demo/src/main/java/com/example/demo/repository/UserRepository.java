package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    
    // ユーザー名で検索
    Optional<User> findByUsername(String username);
    
    // 重複確認
    boolean existsByUsername(String username);
    
    // ロックされているアカウントをチェック（必要に応じて利用）
    boolean existsByUsernameAndIsLocked(String username, boolean isLocked);
    
    // 失敗回数でフィルタリング（管理用に必要なら）
    Optional<User> findByUsernameAndFailedLoginAttemptsGreaterThanEqual(String username, int attempts);
}
