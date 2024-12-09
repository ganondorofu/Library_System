package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.LoginHistory;
import com.example.demo.repository.LoginHistoryRepository;

@Service
public class LoginHistoryService {

    @Autowired
    private LoginHistoryRepository loginHistoryRepository;

    // ユーザーIDに基づいてログイン履歴を取得
    public List<LoginHistory> getLoginHistoriesByUser(Long userId) {
        return loginHistoryRepository.findByUserId(userId);
    }

    // ログイン履歴を保存
    public void saveLoginHistory(LoginHistory loginHistory) {
        loginHistoryRepository.save(loginHistory);
    }
}

