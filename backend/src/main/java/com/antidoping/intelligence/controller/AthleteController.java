package com.antidoping.intelligence.controller;

import com.antidoping.intelligence.dto.PageResponse;
import com.antidoping.intelligence.dto.request.AthleteRequest;
import com.antidoping.intelligence.dto.response.AthleteResponse;
import com.antidoping.intelligence.entity.AthleteStatus;
import com.antidoping.intelligence.entity.RiskLevel;
import com.antidoping.intelligence.service.AthleteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/athletes")
@RequiredArgsConstructor
public class AthleteController {

    private final AthleteService athleteService;

    @GetMapping
    public ResponseEntity<PageResponse<AthleteResponse>> search(
            @RequestParam(required = false) String sport,
            @RequestParam(required = false) AthleteStatus status,
            @RequestParam(required = false) RiskLevel riskLevel,
            @RequestParam(required = false) String query,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(athleteService.search(sport, status, riskLevel, query, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AthleteResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(athleteService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','INVESTIGATOR')")
    public ResponseEntity<AthleteResponse> create(@Valid @RequestBody AthleteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(athleteService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','INVESTIGATOR')")
    public ResponseEntity<AthleteResponse> update(@PathVariable Long id, @Valid @RequestBody AthleteRequest request) {
        return ResponseEntity.ok(athleteService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        athleteService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
