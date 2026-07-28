package com.proflow.proflow.service;

import com.proflow.proflow.config.JwtUtils;
import com.proflow.proflow.dto.AuthRequest;
import com.proflow.proflow.dto.AuthResponse;
import com.proflow.proflow.dto.RegisterRequest;
import com.proflow.proflow.model.postgres.Role;
import com.proflow.proflow.model.postgres.User;
import com.proflow.proflow.repository.postgres.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail()))
            throw new RuntimeException("Email already in use");

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.MEMBER)
                .build();

        userRepository.save(user);
        return new AuthResponse(
                jwtUtils.generateToken(user.getEmail(), user.getRole().name()),
                user.getRole().name()
        );
    }

    public AuthResponse login(AuthRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword()))
            throw new RuntimeException("Invalid credentials");

        return new AuthResponse(
                jwtUtils.generateToken(user.getEmail(), user.getRole().name()),
                user.getRole().name()
        );
    }
}