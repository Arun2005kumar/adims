package com.antidoping.intelligence.repository;

import com.antidoping.intelligence.entity.CasePriority;
import com.antidoping.intelligence.entity.CaseStatus;
import com.antidoping.intelligence.entity.InvestigationCase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface InvestigationCaseRepository extends JpaRepository<InvestigationCase, Long> {

    Optional<InvestigationCase> findByCaseNumber(String caseNumber);

    boolean existsByCaseNumber(String caseNumber);

    @Query("""
        SELECT c FROM InvestigationCase c
        WHERE (:status IS NULL OR c.status = :status)
          AND (:priority IS NULL OR c.priority = :priority)
          AND (:assignedTo IS NULL OR c.assignedInvestigator.id = :assignedTo)
        ORDER BY c.createdAt DESC
        """)
    Page<InvestigationCase> search(@Param("status") CaseStatus status,
                                    @Param("priority") CasePriority priority,
                                    @Param("assignedTo") Long assignedTo,
                                    Pageable pageable);

    List<InvestigationCase> findByAthleteIdOrderByCreatedAtDesc(Long athleteId);

    long countByStatus(CaseStatus status);

    @Query("SELECT COUNT(c) FROM InvestigationCase c WHERE c.athlete.id = :athleteId AND c.status NOT IN ('CLOSED','ARCHIVED')")
    long countOpenCasesByAthlete(@Param("athleteId") Long athleteId);

    long countByCaseNumberStartingWith(String prefix);

    long countByAthleteIdAndStatusIn(Long athleteId, List<CaseStatus> statuses);
}
