package com.antidoping.intelligence.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CaseResponse {
    private Long id;
    private String caseNumber;
    private String title;
    private Long athleteId;
    private String athleteName;
    private Long tipId;
    private String priority;
    private String status;
    private Long assignedInvestigatorId;
    private String assignedInvestigatorName;
    private String description;
    private LocalDate openedDate;
    private LocalDate closedDate;
    private String outcome;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
