package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.model.User;
import com.example.demo.service.UserService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    // ユーザー登録フォームの表示
    @GetMapping("/register")
    public String showRegistrationForm() {
        return "register"; // templates/register.htmlを表示
    }

    // 新規登録処理
    @PostMapping("/add")
    public String registerUser(@Valid User user, BindingResult result) {
        // バリデーションエラーがある場合
        if (result.hasErrors()) {
            return "redirect:/register?error=validation"; // バリデーションエラー時にリダイレクト
        }

        // ユーザー名の重複チェック
        if (userService.existsByUsername(user.getUsername())) {
            return "redirect:/register?error=duplicate"; // ユーザー名重複時にリダイレクト
        }

        // パスワードの暗号化と保存
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userService.saveUser(user);

        // 登録成功後にログインページへリダイレクト
        return "redirect:/login?register=true";
    }

    // ログインページの表示
    @GetMapping("/login")
    public String showLoginPage() {
        return "login"; // templates/login.htmlを表示
    }
}
