package com.antidoping.intelligence.service.impl;

import com.antidoping.intelligence.dto.response.DashboardSummaryResponse;
import com.antidoping.intelligence.entity.*;
import com.antidoping.intelligence.repository.*;
import com.antidoping.intelligence.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final AthleteRepository athleteRepository;
    private final IntelligenceTipRepository tipRepository;
    private final InvestigationCaseRepository caseRepository;

    @Override
    public DashboardSummaryResponse getSummary() {
        Map<String, Long> casesByStatus = new LinkedHashMap<>();
        for (CaseStatus status : CaseStatus.values()) {
            casesByStatus.put(status.name(), caseRepository.countByStatus(status));
        }

        Map<String, Long> athletesByRiskLevel = new LinkedHashMap<>();
        for (RiskLevel level : RiskLevel.values()) {
            athletesByRiskLevel.put(level.name(), athleteRepository.countByRiskLevel(level));
        }

        Map<String, Long> tipsByCategory = new LinkedHashMap<>();
        for (TipCategory category : TipCategory.values()) {
            tipsByCategory.put(category.name(), tipRepository.countByCategory(category));
        }

        long openCases = casesByStatus.getOrDefault("OPEN", 0L) + casesByStatus.getOrDefault("IN_PROGRESS", 0L)
                + casesByStatus.getOrDefault("PENDING_REVIEW", 0L);

        return DashboardSummaryResponse.builder()
                .totalAthletes(athleteRepository.count())
                .highRiskAthletes(athletesByRiskLevel.getOrDefault("HIGH", 0L) + athletesByRiskLevel.getOrDefault("CRITICAL", 0L))
                .openCases(openCases)
                .newTips(tipRepository.countByStatus(TipStatus.NEW))
                .pendingReviewTips(tipRepository.countByStatus(TipStatus.UNDER_REVIEW))
                .totalCases(caseRepository.count())
                .casesByStatus(casesByStatus)
                .athletesByRiskLevel(athletesByRiskLevel)
                .tipsByCategory(tipsByCategory)
                .build();
    }
}
