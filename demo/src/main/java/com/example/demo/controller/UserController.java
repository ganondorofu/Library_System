package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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

    // 新規登録処理
    @PostMapping("/add") // このメソッドが /users/add にマッピングされる
    public String registerUser(@Valid User user, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "register"; // エラーがあれば再度登録画面を表示
        }

        // ユーザー名の重複チェック
        if (userService.existsByUsername(user.getUsername())) {
            model.addAttribute("error", "ユーザー名が既に存在しています");
            return "register";
        }

        // パスワードの暗号化と保存
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userService.saveUser(user);

        return "redirect:/login?register=true"; // 登録成功後、ログインページへリダイレクト

    }
}
