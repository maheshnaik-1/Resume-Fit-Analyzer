package com.resumeanalyzer.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record LoginResponse(
        boolean success,
        String message,
        String name,
        String email
) {
    public static LoginResponse failure(String message) {
        return new LoginResponse(false, message, null, null);
    }

    public static LoginResponse success(String message, String name, String email) {
        return new LoginResponse(true, message, name, email);
    }
}
