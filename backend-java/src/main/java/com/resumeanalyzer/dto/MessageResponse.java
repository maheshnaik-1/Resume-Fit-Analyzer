package com.resumeanalyzer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record MessageResponse(
        @JsonProperty("message") String message
) {}
