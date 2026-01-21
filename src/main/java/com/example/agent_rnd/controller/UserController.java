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

    // 이 내용들이 살아있어야 합니다!
    @GetMapping("/check-email")
    public boolean checkEmail(@RequestParam String email) {
        return userService.checkEmailDuplicate(email);
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id) {
        return userService.getUser(id);
    }
}