package com.example.agent_rnd.dto;

public class EmailAuthDtos {

    public record SendCodeRequest(String email) {}
    public record VerifyCodeRequest(String email, String code) {}

    public record SendCodeResponse(String message) {}
    public record VerifyCodeResponse(boolean verified, String message) {}
}
