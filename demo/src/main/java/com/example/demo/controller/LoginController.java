package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam; // 修正ポイント
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.UserService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/auth")
public class LoginController {

    @Autowired
    private UserService userService;

    /**
     * ログイン処理
     *
     * @param email    ユーザーのメールアドレス
     * @param password ユーザーのパスワード
     * @param session  セッション管理
     * @return ログイン結果
     */
    @PostMapping("/login")
    public String login(@RequestParam String email, @RequestParam String password, HttpSession session) {
        if (userService.authenticate(email, password)) {
            session.setAttribute("user", email);
            return "ログイン成功";
        } else {
            return "ログイン失敗：メールアドレスまたはパスワードが違います";
        }
    }

    /**
     * ログアウト処理
     *
     * @param session セッション管理
     * @return ログアウト結果
     */
    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "ログアウトしました";
    }
}
