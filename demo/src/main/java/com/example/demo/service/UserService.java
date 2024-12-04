package com.example.demo.service;


import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    // コンストラクタインジェクションでUserRepositoryを注入
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // loadUserByUsername メソッド: ユーザー名でユーザー情報を取得
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        if (user.isLocked()) { // アカウントがロックされている場合
            throw new UsernameNotFoundException("Account is locked for username: " + username);
        }

        return user;
    }

    // 新規ユーザー登録のためにユーザーを保存するメソッド
    public void saveUser(User user) {
        userRepository.save(user);  // ユーザーをデータベースに保存
    }

    // ユーザー名が既に存在しているかをチェックするメソッド
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);  // ユーザー名の存在確認
    }

    // ログイン失敗時に失敗回数を増加させるメソッド
    public void increaseFailedAttempts(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);

        if (user.getFailedLoginAttempts() >= 5) { // 失敗回数が規定値を超えたらロック
            user.setLocked(true);
        }

        userRepository.save(user);
    }

    // ログイン成功時に失敗回数をリセットするメソッド
    public void resetFailedAttempts(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        user.setFailedLoginAttempts(0);
        user.setLocked(false);
        userRepository.save(user);
    }

    // アカウントのロックを解除する管理者向けメソッド
    public void unlockAccount(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        user.setLocked(false);
        user.setFailedLoginAttempts(0);
        userRepository.save(user);
    }
    
}
