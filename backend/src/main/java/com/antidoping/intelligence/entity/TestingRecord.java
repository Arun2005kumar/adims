package com.antidoping.intelligence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "testing_records")
public class TestingRecord extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "athlete_id", nullable = false)
    private Athlete athlete;

    @Column(name = "test_date", nullable = false)
    private LocalDate testDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "test_type", nullable = false, length = 30)
    private TestType testType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TestResult result;

    @Column(name = "sample_id", length = 100)
    private String sampleId;

    @Column(name = "lab_name", length = 150)
    private String labName;

    @Column(columnDefinition = "TEXT")
    private String notes;
}
