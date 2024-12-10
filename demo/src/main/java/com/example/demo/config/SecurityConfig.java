package com.example.demo.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
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
            .authorizeHttpRequests(auth -> auth  // authorizeRequests -> authorizeHttpRequests
                .requestMatchers("/", "/login", "/register", "/users/**", "/css/**", "/js/**").permitAll()
                .anyRequest().authenticated()  // 他のすべてのリクエストは認証が必要
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/", true)
                .failureUrl("/login?error")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .permitAll()
            )
            .formLogin(form -> form
                .loginProcessingUrl("/signin")
            );

        return http.build();
    }


}
