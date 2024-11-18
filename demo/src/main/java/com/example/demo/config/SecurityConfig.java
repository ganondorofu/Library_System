package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

@Configuration
public class SecurityConfig {

    // PasswordEncoderのBean登録
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12); // 強度を12に設定
    }

    // SecurityFilterChainのBean登録
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, HandlerMappingIntrospector introspector) throws Exception {
        MvcRequestMatcher loginMatcher = new MvcRequestMatcher(introspector, "/api/auth/login");
        MvcRequestMatcher logoutMatcher = new MvcRequestMatcher(introspector, "/api/auth/logout");
        MvcRequestMatcher registerMatcher = new MvcRequestMatcher(introspector, "/register");

        http.csrf().disable() // 必要に応じてCSRFを有効化
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(loginMatcher, logoutMatcher, registerMatcher).permitAll() // 認証不要エンドポイント
                .anyRequest().authenticated() // その他のリクエストは認証が必要
            )
            .logout(logout -> logout
                .logoutUrl("/api/auth/logout")
                .logoutSuccessUrl("/login")
            );

        return http.build();
    }
}
