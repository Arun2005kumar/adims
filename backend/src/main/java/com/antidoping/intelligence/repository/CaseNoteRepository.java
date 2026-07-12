package com.antidoping.intelligence.repository;

import com.antidoping.intelligence.entity.CaseNote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CaseNoteRepository extends JpaRepository<CaseNote, Long> {
    List<CaseNote> findByInvestigationCaseIdOrderByCreatedAtDesc(Long caseId);
}
