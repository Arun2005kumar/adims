package com.antidoping.intelligence.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestingRecordResponse {
    private Long id;
    private Long athleteId;
    private String athleteName;
    private LocalDate testDate;
    private String testType;
    private String result;
    private String sampleId;
    private String labName;
    private String notes;
}
