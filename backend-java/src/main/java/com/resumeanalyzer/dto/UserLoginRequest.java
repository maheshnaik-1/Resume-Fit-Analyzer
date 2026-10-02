package com.resumeanalyzer.dto;

public record UserLoginRequest(
        String email,
        String password
) {}
