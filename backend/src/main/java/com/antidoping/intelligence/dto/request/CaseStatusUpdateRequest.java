package com.antidoping.intelligence.dto.request;

import com.antidoping.intelligence.entity.CaseStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CaseStatusUpdateRequest {
    @NotNull
    private CaseStatus status;
    private String outcome;
}
