package com.example.demo.config;

import java.time.LocalDateTime;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
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
import com.example.demo.service.UserService;
import com.example.demo.utility.MfaUtil;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final UserService userService;
    private final LoginHistoryService loginHistoryService;
    private final HttpServletRequest request; // IPアドレスやデバイス情報取得用
    private final PasswordEncoder passwordEncoder;

    public CustomAuthenticationProvider(UserService userService, 
                                         LoginHistoryService loginHistoryService,
                                         HttpServletRequest request) {
        this.userService = userService;
        this.loginHistoryService = loginHistoryService;
        this.request = request;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName(); // 入力されたユーザー名を取得
        String password = authentication.getCredentials().toString(); // 入力されたパスワードを取得
        String mfaCode = request.getParameter("mfa"); // フォームからMFAコードを取得

        UserDetails userDetails = userService.loadUserByUsername(username);

        if (userDetails == null) {
            saveLoginHistory(null, false);
            throw new BadCredentialsException("User not found");
        }

        // アカウントがロックされている場合
        if (((User) userDetails).isLocked()) {
            saveLoginHistory((User) userDetails, false);
            throw new DisabledException("Account is locked");
        }

        // パスワードが一致しない場合
        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            userService.increaseFailedAttempts(username); // ログイン失敗回数を増加
            saveLoginHistory((User) userDetails, false);
            throw new BadCredentialsException("Invalid credentials");
        }

        // MFA認証が有効で、シークレットキーが設定されている場合
        User user = (User) userDetails;
        if (user.getMfaSecret() != null && !user.getMfaSecret().isEmpty()) {
        	if (user.getMfaSecret() != null && !user.getMfaSecret().isEmpty()) {
        	    if (mfaCode == null || mfaCode.trim().isEmpty()) {
        	        saveLoginHistory(user, false);
        	        throw new BadCredentialsException("MFA code is required. Please enter your MFA code.");
        	    }
        	    if (!MfaUtil.verifyCode(user.getMfaSecret(), Integer.parseInt(mfaCode))) {
        	        saveLoginHistory(user, false);
        	        throw new BadCredentialsException("Invalid MFA code. Please try again.");
        	    }
        	}


        // ログイン成功時
        userService.resetFailedAttempts(username);
        saveLoginHistory(user, true);

    }
        return new UsernamePasswordAuthenticationToken(
        		username, null, userDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }

    // ログイン履歴の保存
    private void saveLoginHistory(User user, boolean success) {
        LoginHistory history = new LoginHistory();
        history.setUser(user);
        history.setLoginSuccess(success);
        history.setLoginTime(LocalDateTime.now());
        history.setIpAddress(request.getRemoteAddr());
        history.setDeviceInfo(request.getHeader("User-Agent"));

        loginHistoryService.saveLoginHistory(history);
    }
}
