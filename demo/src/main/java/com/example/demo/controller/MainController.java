package com.example.demo.controller; // 必ず package 文はクラスの最初に記載する

import org.springframework.web.bind.annotation.GetMapping; // 必要なインポート
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class MainController {
    @GetMapping("/")
    public String index() {
        return "index"; // クライアントに "index" を返す
    }
}
