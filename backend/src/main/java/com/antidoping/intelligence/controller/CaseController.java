package com.antidoping.intelligence.controller;

import com.antidoping.intelligence.dto.PageResponse;
import com.antidoping.intelligence.dto.request.*;
import com.antidoping.intelligence.dto.response.*;
import com.antidoping.intelligence.entity.CasePriority;
import com.antidoping.intelligence.entity.CaseStatus;
import com.antidoping.intelligence.service.CaseService;
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
@RequestMapping("/api/cases")
@RequiredArgsConstructor
public class CaseController {

    private final CaseService caseService;

    @GetMapping
    public ResponseEntity<PageResponse<CaseResponse>> search(
            @RequestParam(required = false) CaseStatus status,
            @RequestParam(required = false) CasePriority priority,
            @RequestParam(required = false) Long assignedTo,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(caseService.search(status, priority, assignedTo, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CaseResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(caseService.getById(id));
    }

    @GetMapping("/athlete/{athleteId}")
    public ResponseEntity<List<CaseResponse>> getByAthlete(@PathVariable Long athleteId) {
        return ResponseEntity.ok(caseService.getByAthlete(athleteId));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','INVESTIGATOR')")
    public ResponseEntity<CaseResponse> create(@Valid @RequestBody CaseRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(caseService.create(request));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN','INVESTIGATOR')")
    public ResponseEntity<CaseResponse> updateStatus(@PathVariable Long id, @Valid @RequestBody CaseStatusUpdateRequest request) {
        return ResponseEntity.ok(caseService.updateStatus(id, request));
    }

    @PatchMapping("/{id}/assign")
    @PreAuthorize("hasAnyRole('ADMIN','INVESTIGATOR')")
    public ResponseEntity<CaseResponse> assign(@PathVariable Long id, @Valid @RequestBody CaseAssignRequest request) {
        return ResponseEntity.ok(caseService.assign(id, request));
    }

    @GetMapping("/{id}/notes")
    public ResponseEntity<List<CaseNoteResponse>> getNotes(@PathVariable Long id) {
        return ResponseEntity.ok(caseService.getNotes(id));
    }

    @PostMapping("/{id}/notes")
    @PreAuthorize("hasAnyRole('ADMIN','INVESTIGATOR')")
    public ResponseEntity<CaseNoteResponse> addNote(@PathVariable Long id, @Valid @RequestBody CaseNoteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(caseService.addNote(id, request));
    }

    @GetMapping("/{id}/evidence")
    public ResponseEntity<List<EvidenceResponse>> getEvidence(@PathVariable Long id) {
        return ResponseEntity.ok(caseService.getEvidence(id));
    }

    @PostMapping("/{id}/evidence")
    @PreAuthorize("hasAnyRole('ADMIN','INVESTIGATOR')")
    public ResponseEntity<EvidenceResponse> addEvidence(@PathVariable Long id, @Valid @RequestBody EvidenceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(caseService.addEvidence(id, request));
    }
}
