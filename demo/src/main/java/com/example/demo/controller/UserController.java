package com.example.demo.controller;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.model.User;
import com.example.demo.service.UserService;

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

    // ユーザー登録フォームの表示
    @GetMapping("/register")
    public String showRegistrationForm(@RequestParam(value = "error", required = false) String error,
                                       @RequestParam(value = "username", required = false) String username,
                                       @RequestParam(value = "email", required = false) String email,
                                       Model model) {
        model.addAttribute("error", error);
        model.addAttribute("username", username);
        model.addAttribute("email", email);
        return "register"; // templates/register.htmlを表示
    }

    // 新規登録処理
    @PostMapping("/add")
    public String registerUser(@RequestParam String username,
                               @RequestParam String email,
                               @RequestParam String password) {
        try {
            // ユーザー名の重複チェック
            if (userService.existsByUsername(username)) {
                return "redirect:/register?error=username-taken&username=" + username + "&email=" + email;
            }

            // メールアドレスのバリデーション
            if (!email.matches(".+@.+\\..+")) {
                return "redirect:/register?error=email-invalid&username=" + username;
            }

            // パスワードの強度チェック
            if (password.length() < 8) {
                return "redirect:/register?error=password-weak&username=" + username + "&email=" + email;
            }

            // ユーザー作成
            User user = new User();
            user.setUsername(username);
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(password));
            userService.saveUser(user);

            // 成功時、ログインページへリダイレクト
            return "redirect:/login?register=true";

        } catch (Exception e) {
            return "redirect:/register?error=general-error";
        }
    }	

    // signin処理（POSTリクエストでログイン認証）
    @PostMapping("/signin")
    public String signinUser(String username, String password, Model model) {
        try {
            // ユーザー名とパスワードを使って認証を試みる
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
            );

            // 認証に成功した場合、認証情報をセキュリティコンテキストにセット
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // ログイン後、ホームページにリダイレクト
            return "redirect:/"; // ここでリダイレクト先を設定
        } catch (Exception e) {
            // 認証失敗時のエラーメッセージを表示
            model.addAttribute("error", "Invalid username or password");
            return "login"; // ログイン画面に戻る
        }
    }
}
