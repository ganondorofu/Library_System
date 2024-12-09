package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.LoginHistory;
import com.example.demo.repository.LoginHistoryRepository;

@Service
public class LoginHistoryService {

    @Autowired
    private LoginHistoryRepository loginHistoryRepository;

    public void saveLoginHistory(LoginHistory loginHistory) {
        loginHistoryRepository.save(loginHistory);
    }

    // 必要に応じて履歴取得のロジックを追加
}
