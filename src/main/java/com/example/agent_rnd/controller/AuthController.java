package com.example.agent_rnd.controller;

import com.example.agent_rnd.dto.AuthDtos;
import com.example.agent_rnd.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    // 회원가입
    @PostMapping("/company-signup")
    public AuthDtos.CompanySignupResponse companySignup(@RequestBody AuthDtos.CompanySignupRequest req) {
        var r = userService.companySignupAndCreateAdmin(req);
        return new AuthDtos.CompanySignupResponse(r.companyId(), r.adminUserId());
    }
    // 삭제
    @DeleteMapping("/company/{companyId}")
    public ResponseEntity<Void> deleteCompanySignup(@PathVariable Long companyId) {
        userService.deleteCompanySignup(companyId);
        return ResponseEntity.noContent().build();
    }
}
