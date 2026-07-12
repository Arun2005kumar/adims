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
public class EvidenceResponse {
    private Long id;
    private Long caseId;
    private String type;
    private String description;
    private String fileReference;
    private String collectedByUsername;
    private LocalDate collectedDate;
    private LocalDateTime createdAt;
}
