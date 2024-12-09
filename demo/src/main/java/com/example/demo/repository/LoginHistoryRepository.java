package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.LoginHistory;

@Repository
public interface LoginHistoryRepository extends JpaRepository<LoginHistory, Integer> {
    // 必要に応じてカスタムクエリを追加可能
}
