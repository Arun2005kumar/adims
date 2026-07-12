package com.antidoping.intelligence.service.impl;

import com.antidoping.intelligence.dto.request.UserCreateRequest;
import com.antidoping.intelligence.dto.response.UserResponse;
import com.antidoping.intelligence.entity.Role;
import com.antidoping.intelligence.entity.User;
import com.antidoping.intelligence.exception.DuplicateResourceException;
import com.antidoping.intelligence.exception.ResourceNotFoundException;
import com.antidoping.intelligence.repository.UserRepository;
import com.antidoping.intelligence.service.AuditLogService;
import com.antidoping.intelligence.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditLogService auditLogService;

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    public UserResponse getUserById(Long id) {
        return toResponse(findUser(id));
    }

    @Override
    @Transactional
    public UserResponse createUser(UserCreateRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("Username already exists: " + request.getUsername());
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already registered: " + request.getEmail());
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .email(request.getEmail())
                .role(request.getRole())
                .active(true)
                .build();

        User saved = userRepository.save(user);
        auditLogService.log("CREATE_USER", "User", saved.getId(), "Created user " + saved.getUsername());
        return toResponse(saved);
    }

    @Override
    @Transactional
    public UserResponse deactivateUser(Long id) {
        User user = findUser(id);
        user.setActive(false);
        User saved = userRepository.save(user);
        auditLogService.log("DEACTIVATE_USER", "User", id, "Deactivated user " + user.getUsername());
        return toResponse(saved);
    }

    @Override
    @Transactional
    public UserResponse activateUser(Long id) {
        User user = findUser(id);
        user.setActive(true);
        User saved = userRepository.save(user);
        auditLogService.log("ACTIVATE_USER", "User", id, "Activated user " + user.getUsername());
        return toResponse(saved);
    }

    @Override
    public List<UserResponse> getInvestigators() {
        return userRepository.findAll().stream()
                .filter(u -> u.getRole() == Role.INVESTIGATOR && u.isActive())
                .map(this::toResponse)
                .toList();
    }

    private User findUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    private UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .active(user.isActive())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
