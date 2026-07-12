package com.antidoping.intelligence.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CaseAssignRequest {
    @NotNull
    private Long investigatorId;
}
