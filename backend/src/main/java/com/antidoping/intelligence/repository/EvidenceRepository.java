package com.antidoping.intelligence.repository;

import com.antidoping.intelligence.entity.Evidence;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EvidenceRepository extends JpaRepository<Evidence, Long> {
    List<Evidence> findByInvestigationCaseIdOrderByCollectedDateDesc(Long caseId);
}
