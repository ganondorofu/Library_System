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
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }

    // 新規ユーザー登録のためにユーザーを保存するメソッド
    public void saveUser(User user) {
        userRepository.save(user);  // ユーザーをデータベースに保存
    }

    // ユーザー名が既に存在しているかをチェックするメソッド
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);  // ユーザー名の存在確認
    }
}
