package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api")
public class MainController {

    /**
     * インデックスページを表示
     *
     * @return "index" テンプレート名
     */
    @GetMapping("/")
    public String index() {
        return "index"; // templates/index.html を返す
    }
}
