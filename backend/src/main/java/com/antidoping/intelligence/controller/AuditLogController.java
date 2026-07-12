package com.antidoping.intelligence.controller;

import com.antidoping.intelligence.dto.PageResponse;
import com.antidoping.intelligence.dto.response.AuditLogResponse;
import com.antidoping.intelligence.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/audit-logs")
@RequiredArgsConstructor
public class AuditLogController {

    private final AuditLogService auditLogService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PageResponse<AuditLogResponse>> getLogs(@PageableDefault(size = 30) Pageable pageable) {
        return ResponseEntity.ok(auditLogService.getLogs(pageable));
    }
}
