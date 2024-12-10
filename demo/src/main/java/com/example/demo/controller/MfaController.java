package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
        String secretKey = mfaService.generateTemporarySecretKey(username);

        // QRコードのデータ生成
        String qrCodeData = String.format(
                "otpauth://totp/%s?secret=%s&issuer=YourAppName",
                username, secretKey
        );
        String qrCodeImage = MfaUtil.generateQRCodeBase64(qrCodeData);

        // モデルにQRコードとシークレットキーを追加
        model.addAttribute("qrCodeImage", qrCodeImage);
        model.addAttribute("secretKey", secretKey);

        return "mfa-setup"; // mfa-setup.htmlをレンダリング
    }
    @PostMapping("/mfa/verify")
    public String verifyMfa(
            Authentication authentication,
            @RequestParam("verificationCode") int verificationCode,
            Model model) {
        try {
            String username = authentication.getName();
            boolean isVerified = mfaService.verifyAndSaveSecretKey(username, verificationCode);
            model.addAttribute("isVerified", isVerified);
            return "mfa-verification-result";
        } catch (Exception e) {
            // エラーメッセージをモデルに追加して同じテンプレートに渡す
            model.addAttribute("isVerified", false); // エラー時は検証失敗と同様に扱う
            model.addAttribute("error", "An error occurred: " + e.getMessage());
            return "mfa-verification-result";
        }
    }
    }