package com.resumeanalyzer.controller;

import com.resumeanalyzer.dto.LoginResponse;
import com.resumeanalyzer.dto.SignupResponse;
import com.resumeanalyzer.dto.UserLoginRequest;
import com.resumeanalyzer.dto.UserSignupRequest;
import com.resumeanalyzer.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Authentication", description = "Endpoints for user registration and authentication")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    @Operation(summary = "Register a new user", description = "Creates a new user account with normalized email and encrypted password.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "Validation failure or email already exists")
    })
    public ResponseEntity<SignupResponse> signup(@Valid @RequestBody UserSignupRequest request) {
        SignupResponse response = authService.signup(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate user", description = "Authenticates user credentials and returns session details.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login result returned (success flag indicates status)"),
            @ApiResponse(responseCode = "400", description = "Validation failure")
    })
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody UserLoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}

