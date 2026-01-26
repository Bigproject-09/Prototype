package com.example.agent_rnd.controller;

import com.example.agent_rnd.dto.EmailAuthDtos;
import com.example.agent_rnd.service.EmailAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth/email")
public class EmailAuthController {

    private final EmailAuthService emailAuthService;

    @PostMapping("/send")
    public ResponseEntity<EmailAuthDtos.SendCodeResponse> send(@RequestBody EmailAuthDtos.SendCodeRequest req) {
        emailAuthService.sendCode(req.email());
        return ResponseEntity.ok(new EmailAuthDtos.SendCodeResponse("인증코드를 발송했습니다."));
    }

    @PostMapping("/verify")
    public ResponseEntity<EmailAuthDtos.VerifyCodeResponse> verify(@RequestBody EmailAuthDtos.VerifyCodeRequest req) {
        boolean ok = emailAuthService.verifyCode(req.email(), req.code());
        return ResponseEntity.ok(new EmailAuthDtos.VerifyCodeResponse(ok, "인증 완료"));
    }
}
