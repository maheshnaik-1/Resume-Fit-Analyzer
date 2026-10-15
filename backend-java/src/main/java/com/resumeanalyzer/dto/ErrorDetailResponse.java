package com.resumeanalyzer.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorDetailResponse(
        String detail,
        String message,
        Map<String, String> errors
) {
    public ErrorDetailResponse(String detail) {
        this(detail, detail, null);
    }

    public ErrorDetailResponse(String detail, String message) {
        this(detail, message, null);
    }
}

