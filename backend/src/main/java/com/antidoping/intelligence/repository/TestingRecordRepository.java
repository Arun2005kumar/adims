package com.antidoping.intelligence.repository;

import com.antidoping.intelligence.entity.TestResult;
import com.antidoping.intelligence.entity.TestingRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TestingRecordRepository extends JpaRepository<TestingRecord, Long> {
    List<TestingRecord> findByAthleteIdOrderByTestDateDesc(Long athleteId);
    long countByAthleteIdAndResult(Long athleteId, TestResult result);
    Page<TestingRecord> findAllByOrderByTestDateDesc(Pageable pageable);
}
