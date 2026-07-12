package com.antidoping.intelligence.controller;

import com.antidoping.intelligence.dto.response.RiskAssessmentResponse;
import com.antidoping.intelligence.service.RiskAssessmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/risk-assessments")
@RequiredArgsConstructor
public class RiskAssessmentController {

    private final RiskAssessmentService riskAssessmentService;

    @GetMapping("/athlete/{athleteId}")
    public ResponseEntity<List<RiskAssessmentResponse>> getHistory(@PathVariable Long athleteId) {
        return ResponseEntity.ok(riskAssessmentService.getHistory(athleteId));
    }

    @PostMapping("/athlete/{athleteId}/recalculate")
    @PreAuthorize("hasAnyRole('ADMIN','INVESTIGATOR','ANALYST')")
    public ResponseEntity<RiskAssessmentResponse> recalculate(@PathVariable Long athleteId) {
        return ResponseEntity.ok(riskAssessmentService.recalculate(athleteId));
    }
}
