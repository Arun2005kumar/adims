package com.antidoping.intelligence.dto.request;

import com.antidoping.intelligence.entity.CasePriority;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CaseRequest {
    @NotBlank
    private String title;

    private Long athleteId;
    private Long tipId;
    private CasePriority priority;
    private Long assignedInvestigatorId;
    private String description;
}
