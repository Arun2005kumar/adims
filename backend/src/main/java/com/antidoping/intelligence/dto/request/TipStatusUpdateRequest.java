package com.antidoping.intelligence.dto.request;

import com.antidoping.intelligence.entity.TipStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TipStatusUpdateRequest {
    @NotNull
    private TipStatus status;
}
