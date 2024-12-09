package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.demo.service.MfaService;
import com.example.demo.utility.MfaUtil;

@Controller
public class MfaController {

    @Autowired
    private MfaService mfaService;

    @GetMapping("/mfa/setup")
    public String setupMfa(Authentication authentication, Model model) {
        String username = authentication.getName();

        // シークレットキーの生成と保存
        String secretKey = mfaService.generateAndSaveSecretKey(username);

        // QRコード生成
        String qrCodeData = String.format(
                "otpauth://totp/%s?secret=%s&issuer=YourAppName",
                username, secretKey
        );
        String qrCodeImage = MfaUtil.generateQRCodeBase64(qrCodeData);

        model.addAttribute("qrCodeImage", qrCodeImage);
        model.addAttribute("secretKey", secretKey);

        return "mfa-setup";
    }

    @PostMapping("/mfa/verify")
    public String verifyMfa(String username, int verificationCode, Model model) {
        boolean isVerified = mfaService.verifyCode(username, verificationCode);
        model.addAttribute("isVerified", isVerified);
        return "mfa-verification-result";
    }
}
