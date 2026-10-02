package com.resumeanalyzer.service;

import com.resumeanalyzer.dto.LoginResponse;
import com.resumeanalyzer.dto.SignupResponse;
import com.resumeanalyzer.dto.UserLoginRequest;
import com.resumeanalyzer.dto.UserSignupRequest;
import com.resumeanalyzer.entity.User;
import com.resumeanalyzer.exception.DuplicateEmailException;
import com.resumeanalyzer.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public SignupResponse signup(UserSignupRequest request) {
        String cleanEmail = normalizeEmail(request.email());

        if (userRepository.existsByNormalizedEmail(cleanEmail)) {
            throw new DuplicateEmailException("Email already exists");
        }

        String hashedPassword = passwordEncoder.encode(request.password());
        User user = new User(request.name(), cleanEmail, hashedPassword);
        userRepository.save(user);

        return new SignupResponse("User registered successfully");
    }

    @Transactional(readOnly = true)
    public LoginResponse login(UserLoginRequest request) {
        String cleanEmail = normalizeEmail(request.email());

        Optional<User> userOptional = userRepository.findByNormalizedEmail(cleanEmail);
        if (userOptional.isEmpty()) {
            return LoginResponse.failure("User not found");
        }

        User user = userOptional.get();
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            return LoginResponse.failure("Incorrect password");
        }

        return LoginResponse.success("Login successful", user.getName(), user.getEmail());
    }

    private String normalizeEmail(String email) {
        return email != null ? email.trim().toLowerCase() : "";
    }
}
