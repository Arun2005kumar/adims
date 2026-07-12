package com.antidoping.intelligence.repository;

import com.antidoping.intelligence.entity.RiskAssessment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RiskAssessmentRepository extends JpaRepository<RiskAssessment, Long> {
    List<RiskAssessment> findByAthleteIdOrderByAssessmentDateDesc(Long athleteId);

    java.util.Optional<RiskAssessment> findFirstByAthleteIdOrderByAssessmentDateDesc(Long athleteId);
}
