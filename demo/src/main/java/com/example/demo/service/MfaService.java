package com.example.demo.service;

import org.springframework.stereotype.Service;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.utility.MfaUtil;

@Service
public class MfaService {

    private final UserRepository userRepository;

    public MfaService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String generateAndSaveSecretKey(String username) {
        String secretKey = MfaUtil.generateSecretKey();

        // データベースにシークレットキーを保存
        userRepository.updateMfaSecret(username, secretKey);

        return secretKey;
    }

    public boolean verifyCode(String username, int verificationCode) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        String secretKey = user.getMfaSecret();
        if (secretKey == null) {
            throw new RuntimeException("Secret key not found for user: " + username);
        }

        return MfaUtil.verifyCode(secretKey, verificationCode);
    }
}

