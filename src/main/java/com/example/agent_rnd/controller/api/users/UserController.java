package com.example.agent_rnd.controller;

import com.example.agent_rnd.domain.user.User;
import com.example.agent_rnd.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 1. 이메일 중복 확인
    // 사용법: /api/users/check-email?email=test@test.com
    @GetMapping("/check-email")
    public boolean checkEmail(@RequestParam String email) {
        return userService.checkEmailDuplicate(email);
    }

    // 2. 회원 정보 조회 (테스트용)
    // 사용법: /api/users/1
    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id) {
        return userService.getUser(id);
    }

    // 3. 계정 삭제(계정만)
    // 사용법: DELETE http://localhost:8080/api/users/1
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }

    // 4. 계정 삭제(회사 포함)
    // 사용법: DELETE http://localhost:8080/api/admin/companies/1

}