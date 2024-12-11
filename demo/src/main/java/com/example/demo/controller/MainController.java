package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MainController {
	@GetMapping("/")
    public String index() {
        return "index"; // templates/index.htmlをレンダリング
    }
	
	// ログインページの表示
    @GetMapping("/login")
    public String showLoginPage(
        @RequestParam(value = "error", required = false) String error,
        @RequestParam(value = "logout", required = false) String logout,
        Model model) {
        
        System.out.println("Received error parameter: " + error);

    	
        model.addAttribute("error", error);
        model.addAttribute("logout", logout);
        return "login"; // templates/login.htmlを表示
    }

    @GetMapping("/register")
    public String register(@RequestParam(value = "error", required = false) String error,
                           Model model) {
        // エラーメッセージをテンプレートに渡す
        model.addAttribute("error", error);

        // 初期値をテンプレートに渡す（再入力補助用）
        if (!model.containsAttribute("username")) {
            model.addAttribute("username", "");
        }
        if (!model.containsAttribute("email")) {
            model.addAttribute("email", "");
        }

        return "register"; // templates/register.htmlをレンダリング
    }

    @GetMapping("/error")
    public String error() {
        return "error"; // templates/register.htmlをレンダリング
    }
}
