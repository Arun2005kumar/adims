package com.antidoping.intelligence.service;

import com.antidoping.intelligence.dto.request.LoginRequest;
import com.antidoping.intelligence.dto.response.LoginResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);
}
