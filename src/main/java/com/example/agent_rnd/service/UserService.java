package com.example.agent_rnd.service;

import com.example.agent_rnd.domain.user.User;
import com.example.agent_rnd.domain.user.dto.LoginRequest;
import com.example.agent_rnd.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    // BCrypt 직접 사용 (SecurityConfig 사용 안 함)
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // 이메일 중복 체크 (회원가입용)
    public boolean checkEmailDuplicate(String email) {
        return userRepository.existsByEmail(email);
    }

    // 회원 단건 조회 (테스트용)
    public User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("회원 정보가 없습니다."));
    }

    // 로그인 (해시 기반 검증 + 401 처리)
    public User login(LoginRequest request) {

        // 1. 이메일 조회
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.UNAUTHORIZED,
                                "아이디 또는 비밀번호가 올바르지 않습니다."
                        )
                );

        // 2. 비밀번호 검증 (평문 vs 해시)
        if (!passwordEncoder.matches(
                request.getPassword(), // 입력한 평문
                user.getPassword()     // DB의 BCrypt 해시
        )) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "아이디 또는 비밀번호가 올바르지 않습니다."
            );
        }

        // 3. 로그인 성공
        return user;
    }
}
