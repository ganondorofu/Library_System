package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    // PasswordEncoderのBean登録
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // SecurityFilterChainのBean登録
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf().disable() // CSRFを無効化（必要に応じて有効化）
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/login", "/api/auth/logout", "/register").permitAll() // 認証不要なエンドポイント
                .anyRequest().authenticated() // それ以外は認証が必要
            )
            .logout(logout -> logout
                .logoutUrl("/api/auth/logout") // ログアウトエンドポイント
                .logoutSuccessUrl("/login") // ログアウト成功後のリダイレクト先
            );

        return http.build();
    }
}
