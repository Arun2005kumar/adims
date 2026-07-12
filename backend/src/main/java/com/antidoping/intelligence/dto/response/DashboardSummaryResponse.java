package com.antidoping.intelligence.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardSummaryResponse {
    private long totalAthletes;
    private long highRiskAthletes;
    private long openCases;
    private long newTips;
    private long totalCases;
    private long pendingReviewTips;
    private Map<String, Long> casesByStatus;
    private Map<String, Long> athletesByRiskLevel;
    private Map<String, Long> tipsByCategory;
}
