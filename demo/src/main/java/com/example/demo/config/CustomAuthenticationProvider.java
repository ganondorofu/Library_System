package com.example.demo.config;

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

import com.example.demo.model.User;
import com.example.demo.service.UserService;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    // PasswordEncoderを直接インスタンス化
    public CustomAuthenticationProvider(UserService userService) {
        this.userService = userService;
        this.passwordEncoder = new BCryptPasswordEncoder(); // PasswordEncoderを直接初期化
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    	System.out.println("login");
        String username = authentication.getName(); // 入力されたユーザー名を取得
        String password = authentication.getCredentials().toString(); // 入力されたパスワードを取得

        // ユーザー名でユーザー情報を取得
        UserDetails userDetails = userService.loadUserByUsername(username);
        

        // アカウントがロックされている場合
        if (((User) userDetails).isLocked()) {
            throw new DisabledException("Account is locked");
        }

        // パスワードが一致しない場合
        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
        	System.out.println("miss");
            userService.increaseFailedAttempts(username); // ログイン失敗回数を増加
            throw new BadCredentialsException("Invalid credentials");
        }

        // 認証成功時に失敗回数をリセット
        userService.resetFailedAttempts(username);

        // 認証トークンを作成して返却
        return new UsernamePasswordAuthenticationToken(
                username,
                null,
                userDetails.getAuthorities()
        );
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
