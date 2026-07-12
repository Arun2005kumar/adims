package com.antidoping.intelligence.dto.request;

import com.antidoping.intelligence.entity.EvidenceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class EvidenceRequest {
    @NotNull
    private EvidenceType type;

    @NotBlank
    private String description;

    private String fileReference;
    private LocalDate collectedDate;
}
