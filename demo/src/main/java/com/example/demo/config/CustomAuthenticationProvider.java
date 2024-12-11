package com.example.demo.config;

import java.time.LocalDateTime;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.demo.model.LoginHistory;
import com.example.demo.model.User;
import com.example.demo.service.LoginHistoryService;
import com.example.demo.service.MfaService;
import com.example.demo.service.UserService;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final UserService userService;
    private final LoginHistoryService loginHistoryService;
    private final HttpServletRequest request; // IPアドレスやデバイス情報取得用
    private final PasswordEncoder passwordEncoder;
    private final MfaService mfaService;

    public CustomAuthenticationProvider(UserService userService, 
                                         LoginHistoryService loginHistoryService,
                                         HttpServletRequest request,
                                         MfaService mfaService) {
        this.userService = userService;
        this.loginHistoryService = loginHistoryService;
        this.request = request;
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.mfaService = mfaService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        UserDetails userDetails = userService.loadUserByUsername(username);

        // ユーザーが存在しない場合の処理
        if (userDetails == null) {
            saveLoginHistory(null, false);
            throw new BadCredentialsException("bad-credentials");
        }

        User user = (User) userDetails;

        // アカウントがロックされている場合
        if (user.isLocked()) {
            saveLoginHistory(user, false);
            throw new BadCredentialsException("bad-credentials");
        }

        // パスワードが一致しない場合
        if (!passwordEncoder.matches(password, user.getPassword())) {
            userService.increaseFailedAttempts(username);
            saveLoginHistory(user, false);
            throw new BadCredentialsException("bad-credentials");
        }

        // MFAが設定されている場合
        if (user.getMfaSecret() != null) {
            String mfaCode = getMfaCodeFromSession();

            // MFAコードが提供されていない場合
            if (mfaCode == null || mfaCode.isEmpty()) {
                saveLoginHistory(user, false);
                throw new BadCredentialsException("bad-credentials");
            }

            // MFAコードの検証
            try {
                int mfaCodeInt = Integer.parseInt(mfaCode);
                boolean mfaVerified = mfaService.verifyCode(user.getUsername(), mfaCodeInt);
                if (!mfaVerified) {
                    saveLoginHistory(user, false);
                    throw new BadCredentialsException("bad-credentials");
                }
            } catch (NumberFormatException e) {
                saveLoginHistory(user, false);
                throw new BadCredentialsException("bad-credentials");
            }
        }

        // ログイン成功時
        userService.resetFailedAttempts(username);
        saveLoginHistory(user, true);

        return new UsernamePasswordAuthenticationToken(username, null, userDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }

    private void saveLoginHistory(User user, boolean success) {
        LoginHistory history = new LoginHistory();
        history.setUser(user);
        history.setLoginSuccess(success);
        history.setLoginTime(LocalDateTime.now());
        history.setIpAddress(request.getRemoteAddr());
        history.setDeviceInfo(request.getHeader("User-Agent"));
        loginHistoryService.saveLoginHistory(history);
    }

    private String getMfaCodeFromSession() {
        return request.getParameter("mfa");
    }
}
