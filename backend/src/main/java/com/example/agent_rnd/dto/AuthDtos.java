package com.example.agent_rnd.dto;

public class AuthDtos {
    public record CompanySignupRequest(
            String companyName,
            String businessRegNo,
            String openDate,   // YYYYMMDD
            String ceoName,
            String email,
            String password,
            String passwordConfirm,
            Integer planId
    ) {}

    public record CompanySignupResponse(Long companyId, Long adminUserId) {}
}
