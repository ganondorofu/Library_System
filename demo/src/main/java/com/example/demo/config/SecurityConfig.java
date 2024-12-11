package com.example.demo.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.example.demo.service.UserService;

@Configuration
public class SecurityConfig {

    private final CustomAuthenticationProvider customAuthenticationProvider;

    public SecurityConfig(UserService userService, CustomAuthenticationProvider customAuthenticationProvider) {
        this.customAuthenticationProvider = customAuthenticationProvider;
    }

    // PasswordEncoderを定義（BCryptでパスワードを暗号化）
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); 
    }

    // カスタム認証プロバイダを優先的に登録したAuthenticationManager
    @Bean
    public AuthenticationManager authenticationManager() {
        // カスタムプロバイダを優先的に登録
        return new ProviderManager(List.of(customAuthenticationProvider));
    }

    // セキュリティの設定（HttpSecurityの設定）
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/login", "/register", "/users/**", "/css/**", "/js/**").permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/signin") // ログイン処理のエンドポイント
                .defaultSuccessUrl("/", true)
                .failureHandler((request, response, exception) -> {
                    String error = "unknown"; // デフォルトエラー
                    if (exception instanceof BadCredentialsException) {
                        error = "bad-credentials";
                    } else if (exception instanceof DisabledException) {
                        error = "account-locked";
                    }
                    response.sendRedirect("/login?error=" + error);
                })
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .permitAll()
            );

        return http.build();
    }


}
