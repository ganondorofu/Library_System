package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {
	@GetMapping("/")
    public String index() {
        return "index"; // templates/index.htmlをレンダリング
    }
    @GetMapping("/error")
    public String error() {
        return "error"; // templates/register.htmlをレンダリング
    }
    
    @GetMapping("/login")
    public String login() {
        return "login"; // templates/login.htmlをレンダリング
    }

    @GetMapping("/register")
    public String register() {
        return "register"; // templates/register.htmlをレンダリング
    }
}
