package com.example.demo.controller;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final AuthenticationManager authenticationManager;

    public UserController(UserService userService, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
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

    // signin処理（POSTリクエストでログイン認証）
    @PostMapping("/signin")
    public String signinUser(String username, String password, Model model) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            return "redirect:/dashboard"; // ダッシュボードへリダイレクト
        } catch (Exception e) {
            model.addAttribute("error", "Invalid username or password. Please try again.");
            return "login"; // 再度ログイン画面を表示
        }
    }

}
