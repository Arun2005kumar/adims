package com.antidoping.intelligence.dto.request;

import com.antidoping.intelligence.entity.AthleteStatus;
import com.antidoping.intelligence.entity.TestingPoolType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class AthleteRequest {
    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    private LocalDate dateOfBirth;
    private String gender;
    private String nationality;

    @NotBlank
    private String sport;

    private String discipline;

    @NotNull
    private TestingPoolType testingPool;

    private AthleteStatus status;
}
