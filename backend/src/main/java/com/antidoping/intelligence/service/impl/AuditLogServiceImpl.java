package com.antidoping.intelligence.service.impl;

import com.antidoping.intelligence.dto.PageResponse;
import com.antidoping.intelligence.dto.response.AuditLogResponse;
import com.antidoping.intelligence.entity.AuditLog;
import com.antidoping.intelligence.entity.User;
import com.antidoping.intelligence.repository.AuditLogRepository;
import com.antidoping.intelligence.repository.UserRepository;
import com.antidoping.intelligence.service.AuditLogService;
import com.antidoping.intelligence.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void log(String action, String entityType, Long entityId, String details) {
        User user = null;
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId != null) {
            user = userRepository.findById(userId).orElse(null);
        }

        AuditLog log = AuditLog.builder()
                .user(user)
                .action(action)
                .entityType(entityType)
                .entityId(entityId)
                .details(details)
                .build();

        auditLogRepository.save(log);
    }

    @Override
    public PageResponse<AuditLogResponse> getLogs(Pageable pageable) {
        Page<AuditLog> page = auditLogRepository.findAllByOrderByCreatedAtDesc(pageable);
        Page<AuditLogResponse> mapped = page.map(this::toResponse);
        return PageResponse.from(mapped);
    }

    private AuditLogResponse toResponse(AuditLog log) {
        return AuditLogResponse.builder()
                .id(log.getId())
                .username(log.getUser() != null ? log.getUser().getUsername() : "SYSTEM/ANONYMOUS")
                .action(log.getAction())
                .entityType(log.getEntityType())
                .entityId(log.getEntityId())
                .details(log.getDetails())
                .ipAddress(log.getIpAddress())
                .createdAt(log.getCreatedAt())
                .build();
    }
}
