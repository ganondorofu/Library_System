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
	    @RequestParam(value = "username", required = false) String username,
	    Model model) {

	    System.out.println("Received error parameter: " + error);

	    model.addAttribute("error", error);
	    model.addAttribute("logout", logout);
	    model.addAttribute("username", username); // ユーザー名をテンプレートに渡す
	    return "login"; // templates/login.htmlを表示
	}

    
    @GetMapping("/register")
    public String showRegistrationForm(
            @RequestParam(value = "username", required = false) String username,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "error", required = false) String error,
            Model model) {
        model.addAttribute("username", username);
        model.addAttribute("email", email);
        model.addAttribute("error", error);
        return "register"; // templates/register.htmlを表示
    }

    @GetMapping("/error")
    public String error() {
        return "error"; // templates/register.htmlをレンダリング
    }
}
