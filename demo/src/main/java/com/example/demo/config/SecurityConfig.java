package com.example.demo.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.example.demo.service.UserService;

@Configuration
public class SecurityConfig {

    private final UserService userService;
    private final CustomAuthenticationProvider customAuthenticationProvider;

    // コンストラクタインジェクションでUserServiceとCustomAuthenticationProviderを注入
    public SecurityConfig(UserService userService, CustomAuthenticationProvider customAuthenticationProvider) {
        this.userService = userService;
        this.customAuthenticationProvider = customAuthenticationProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // BCryptでパスワードを暗号化
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userService); // UserServiceを設定
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        // カスタムプロバイダを優先的に登録
        return new ProviderManager(List.of(customAuthenticationProvider, daoAuthenticationProvider()));
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 必要に応じてCSRFを無効化（開発環境用）。本番環境では有効化すること。
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/login", "/register", "/users/**", "/css/**", "/js/**").permitAll() // 認証不要のパス
                .anyRequest().authenticated() // それ以外のリクエストは認証が必要
            )
            .formLogin(form -> form
                .loginPage("/login") // カスタムログインページ
                .defaultSuccessUrl("/", true) // ログイン成功後の遷移先
                .failureUrl("/login?error") // ログイン失敗時のリダイレクト先
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true") // ログアウト後のリダイレクト先
                .permitAll()
            );

        return http.build();
    }
}
