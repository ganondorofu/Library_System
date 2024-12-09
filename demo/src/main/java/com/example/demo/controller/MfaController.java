package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.service.MfaService;
import com.example.demo.service.MfaService.MfaSecretNotFoundException;
import com.example.demo.service.MfaService.UserNotFoundException;
import com.example.demo.utility.MfaUtil;

@Controller
public class MfaController {

    @Autowired
    private MfaService mfaService;

    /**
     * MFAのセットアップ画面を表示
     *
     * @param authentication ログイン情報
     * @param model モデル
     * @return QRコードを含むセットアップ画面
     */
    @GetMapping("/mfa/setup")
    public String setupMfa(Authentication authentication, Model model) {
        String username = authentication.getName();

        try {
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
        } catch (UserNotFoundException e) {
            model.addAttribute("error", "User not found. Please contact support.");
            return "error";
        } catch (Exception e) {
            model.addAttribute("error", "An unexpected error occurred: " + e.getMessage());
            return "error";
        }
    }

    /**
     * MFAコードの検証処理
     *
     * @param authentication ログイン情報
     * @param verificationCode 入力されたMFAコード
     * @param model モデル
     * @return 検証結果画面
     */
    @PostMapping("/mfa/verify")
    public String verifyMfa(
            Authentication authentication,
            @RequestParam("verificationCode") int verificationCode,
            Model model) {
        String username = authentication.getName();

        try {
            // MFAコードの検証
            boolean isVerified = mfaService.verifyAndSaveSecretKey(username, verificationCode);

            // 検証結果をモデルに追加
            model.addAttribute("isVerified", isVerified);
            model.addAttribute("message", isVerified
                    ? "MFA verification successful."
                    : "MFA verification failed. Please try again.");
        } catch (MfaSecretNotFoundException e) {
            model.addAttribute("isVerified", false);
            model.addAttribute("message", "MFA setup is not complete. Please set up MFA first.");
        } catch (UserNotFoundException e) {
            model.addAttribute("isVerified", false);
            model.addAttribute("message", "User not found. Please contact support.");
        } catch (Exception e) {
            model.addAttribute("isVerified", false);
            model.addAttribute("message", "An unexpected error occurred: " + e.getMessage());
        }

        return "mfa-verification-result";
    }
}
