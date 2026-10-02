package com.resumeanalyzer.dto;

public record UserSignupRequest(
        String name,
        String email,
        String password
) {}
