package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.model.LoginHistory;
import com.example.demo.model.User;
import com.example.demo.service.LoginHistoryService;
import com.example.demo.service.UserService;

@Controller
@RequestMapping("/login-history")
public class LoginHistoryController {

    @Autowired
    private LoginHistoryService loginHistoryService;

    @Autowired
    private UserService userService; // ユーザー情報を取得するサービス

    @GetMapping
    public String viewMyLoginHistory(Authentication authentication, Model model) {
        // Authentication からユーザー名を取得
        String username = authentication.getName();
        
        // ユーザー名から User エンティティを取得
        User user = userService.findByUsername(username);

        // ユーザーのログイン履歴を取得
        List<LoginHistory> loginHistories = loginHistoryService.getLoginHistoriesByUser(user.getId());
        model.addAttribute("loginHistories", loginHistories);

        return "login-history";
    }
}
