package com.example.demo.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        return user;
    }
    // ユーザー名で検索
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
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
    @Transactional
    public void increaseFailedAttempts(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        
        System.out.println("Current failed attempts: " + user.getFailedLoginAttempts());
        
        user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);

        if (user.getFailedLoginAttempts() >= 5) { // 失敗回数が規定値を超えたらロック
            user.setLocked(true);
            System.out.println("User locked: " + username);
        }

        userRepository.save(user);
        System.out.println("Failed attempts updated to: " + user.getFailedLoginAttempts());
    }


    // ログイン成功時に失敗回数をリセットするメソッド
    @Transactional
    public void resetFailedAttempts(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        user.setFailedLoginAttempts(0);
        user.setLocked(false);
        userRepository.save(user);
    }

    // アカウントのロックを解除する管理者向けメソッド
    @Transactional
    public void unlockAccount(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        user.setLocked(false);
        user.setFailedLoginAttempts(0);
        userRepository.save(user);
    }
}
