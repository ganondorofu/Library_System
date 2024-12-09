package com.example.demo.controller;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.demo.service.UserService;

@Controller
public class DashboardController {

    private final UserService userService;

    public DashboardController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/dashboard")
    public String showDashboard(Principal principal) {
        String username = principal.getName();
        String role = userService.getUserRole(username);

        if (role == null) {
            return "redirect:/error"; // ロールがnullの場合、エラーページへリダイレクト
        }

        switch (role.toUpperCase()) {
            case "USER":
                return "dashboard-user"; // 一般ユーザー向けテンプレート
            case "ADMIN":
                return "dashboard-admin"; // 管理者向けテンプレート
            default:
                return "redirect:/error"; // 未知のロールはエラーページ
        }
    }

}
