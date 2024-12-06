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

    // PasswordEncoderを定義（BCryptでパスワードを暗号化）
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // BCryptでパスワードを暗号化
    }

    // DaoAuthenticationProviderの設定（デフォルトのユーザー認証用プロバイダ）
    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userService); // UserServiceを設定
        provider.setPasswordEncoder(passwordEncoder()); // パスワードエンコーダを設定
        return provider;
    }

    // カスタム認証プロバイダを優先的に登録したAuthenticationManager
    @Bean
    public AuthenticationManager authenticationManager() {
        // カスタム認証プロバイダとデフォルトの認証プロバイダを両方使用
        return new ProviderManager(List.of(customAuthenticationProvider, daoAuthenticationProvider()));
    }

    // セキュリティの設定（HttpSecurityの設定）
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
                .permitAll() // 認証不要の設定
            )
            .logout(logout -> logout
                .logoutUrl("/logout") // ログアウトURL
                .logoutSuccessUrl("/login?logout=true") // ログアウト後のリダイレクト先
                .permitAll() // ログアウト処理は全員アクセス可
            );

        return http.build();
    }
}
