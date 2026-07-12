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
public class CaseNoteResponse {
    private Long id;
    private Long caseId;
    private String authorUsername;
    private String note;
    private LocalDateTime createdAt;
}
