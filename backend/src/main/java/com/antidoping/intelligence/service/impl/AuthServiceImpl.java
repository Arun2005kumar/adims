package com.antidoping.intelligence.service.impl;

import com.antidoping.intelligence.dto.request.LoginRequest;
import com.antidoping.intelligence.dto.response.LoginResponse;
import com.antidoping.intelligence.security.JwtService;
import com.antidoping.intelligence.security.UserPrincipal;
import com.antidoping.intelligence.service.AuditLogService;
import com.antidoping.intelligence.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final AuditLogService auditLogService;

    @Override
    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        String token = jwtService.generateToken(principal);

        auditLogService.log("LOGIN", "User", principal.getId(), "User logged in: " + principal.getUsername());

        return LoginResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .userId(principal.getId())
                .username(principal.getUsername())
                .fullName(principal.getFullName())
                .role(principal.getRole())
                .build();
    }
}
