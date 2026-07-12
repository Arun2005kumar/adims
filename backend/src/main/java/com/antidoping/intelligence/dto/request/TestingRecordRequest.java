package com.antidoping.intelligence.dto.request;

import com.antidoping.intelligence.entity.TestResult;
import com.antidoping.intelligence.entity.TestType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class TestingRecordRequest {
    @NotNull
    private Long athleteId;

    private LocalDate testDate;

    @NotNull
    private TestType testType;

    private TestResult result;
    private String sampleId;
    private String labName;
    private String notes;
}
