package com.antidoping.intelligence.service;

import com.antidoping.intelligence.dto.PageResponse;
import com.antidoping.intelligence.dto.response.AuditLogResponse;
import org.springframework.data.domain.Pageable;

public interface AuditLogService {
    void log(String action, String entityType, Long entityId, String details);
    PageResponse<AuditLogResponse> getLogs(Pageable pageable);
}
