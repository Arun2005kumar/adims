package com.antidoping.intelligence.dto.request;

import com.antidoping.intelligence.entity.TipCategory;
import com.antidoping.intelligence.entity.TipSourceType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TipRequest {
    @NotNull
    private TipSourceType sourceType;

    private String sourceContact;
    private Long athleteId;

    @NotNull
    private TipCategory category;

    @NotBlank
    private String description;

    @Min(1) @Max(5)
    private Integer credibilityScore;
}
