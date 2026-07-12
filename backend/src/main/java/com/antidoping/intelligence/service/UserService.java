package com.antidoping.intelligence.service;

import com.antidoping.intelligence.dto.request.UserCreateRequest;
import com.antidoping.intelligence.dto.response.UserResponse;

import java.util.List;

public interface UserService {
    List<UserResponse> getAllUsers();
    UserResponse getUserById(Long id);
    UserResponse createUser(UserCreateRequest request);
    UserResponse deactivateUser(Long id);
    UserResponse activateUser(Long id);
    List<UserResponse> getInvestigators();
}
