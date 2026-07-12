package com.antidoping.intelligence.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TipResponse {
    private Long id;
    private String sourceType;
    private String sourceContact;
    private Long athleteId;
    private String athleteName;
    private String category;
    private String description;
    private Integer credibilityScore;
    private String status;
    private String submittedByUsername;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
