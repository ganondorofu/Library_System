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

    public CustomAuthenticationProvider(UserService userService) {
        this.userService = userService;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName(); // 入力されたユーザー名を取得
        String password = authentication.getCredentials().toString(); // 入力されたパスワードを取得

        UserDetails userDetails = userService.loadUserByUsername(username);

        // アカウントがロックされている場合
        if (((User) userDetails).isLocked()) {
            throw new DisabledException("Account is locked");
        }

        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            userService.increaseFailedAttempts(username); // ログイン失敗回数を増加
            throw new BadCredentialsException("Invalid credentials");
        }

        userService.resetFailedAttempts(username);

        return new UsernamePasswordAuthenticationToken(
                username, null, userDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
