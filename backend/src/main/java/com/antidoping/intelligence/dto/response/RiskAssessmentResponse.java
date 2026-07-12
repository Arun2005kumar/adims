package com.antidoping.intelligence.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RiskAssessmentResponse {
    private Long id;
    private Long athleteId;
    private String athleteName;
    private LocalDate assessmentDate;
    private Integer missedTestsCount;
    private Integer priorViolationsCount;
    private Integer openTipsCount;
    private Integer openCasesCount;
    private BigDecimal riskScore;
    private String riskLevel;
    private String assessedByUsername;
    private String notes;
}
