package com.antidoping.intelligence.controller;

import com.antidoping.intelligence.dto.PageResponse;
import com.antidoping.intelligence.dto.request.TipRequest;
import com.antidoping.intelligence.dto.request.TipStatusUpdateRequest;
import com.antidoping.intelligence.dto.response.CaseResponse;
import com.antidoping.intelligence.dto.response.TipResponse;
import com.antidoping.intelligence.entity.TipCategory;
import com.antidoping.intelligence.entity.TipStatus;
import com.antidoping.intelligence.service.TipService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tips")
@RequiredArgsConstructor
public class TipController {

    private final TipService tipService;

    @GetMapping
    public ResponseEntity<PageResponse<TipResponse>> search(
            @RequestParam(required = false) TipStatus status,
            @RequestParam(required = false) TipCategory category,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(tipService.search(status, category, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TipResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(tipService.getById(id));
    }

    @GetMapping("/athlete/{athleteId}")
    public ResponseEntity<List<TipResponse>> getByAthlete(@PathVariable Long athleteId) {
        return ResponseEntity.ok(tipService.getByAthlete(athleteId));
    }

    @PostMapping
    public ResponseEntity<TipResponse> submit(@Valid @RequestBody TipRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tipService.submit(request, false));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN','INVESTIGATOR','ANALYST')")
    public ResponseEntity<TipResponse> updateStatus(@PathVariable Long id, @Valid @RequestBody TipStatusUpdateRequest request) {
        return ResponseEntity.ok(tipService.updateStatus(id, request.getStatus()));
    }

    @PostMapping("/{id}/escalate")
    @PreAuthorize("hasAnyRole('ADMIN','INVESTIGATOR')")
    public ResponseEntity<CaseResponse> escalate(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tipService.escalateToCase(id));
    }
}
