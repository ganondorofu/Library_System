package com.example.demo.service;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.utility.MfaUtil;

@Service
public class MfaService {

    private final UserRepository userRepository;
    private final ConcurrentHashMap<String, String> temporarySecrets = new ConcurrentHashMap<>();

    public MfaService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * 一時的なシークレットキーを生成し、キャッシュに保存
     */
    public String generateTemporarySecretKey(String username) {
        String secretKey = MfaUtil.generateSecretKey();
        temporarySecrets.put(username, secretKey);
        return secretKey;
    }

    /**
     * MFAコードの検証とシークレットキーの保存
     */
    @Transactional
    public boolean verifyAndSaveSecretKey(String username, int verificationCode) {
        // 一時的なシークレットキーを取得
        String temporarySecretKey = temporarySecrets.get(username);

        if (temporarySecretKey == null) {
            throw new MfaSecretNotFoundException("Temporary MFA secret key not found for user: " + username);
        }

        // コードを検証
        if (!MfaUtil.verifyCode(temporarySecretKey, verificationCode)) {
            return false;
        }

        // ユーザーを取得し、シークレットキーを保存
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + username));

        user.setMfaSecret(temporarySecretKey);
        userRepository.save(user);

        // 一時的なキーを削除
        temporarySecrets.remove(username);
        return true;
    }

    public static class UserNotFoundException extends RuntimeException {
        public UserNotFoundException(String message) {
            super(message);
        }
    }

    public static class MfaSecretNotFoundException extends RuntimeException {
        public MfaSecretNotFoundException(String message) {
            super(message);
        }
    }
}
