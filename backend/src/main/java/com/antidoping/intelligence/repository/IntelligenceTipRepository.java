package com.antidoping.intelligence.repository;

import com.antidoping.intelligence.entity.IntelligenceTip;
import com.antidoping.intelligence.entity.TipStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IntelligenceTipRepository extends JpaRepository<IntelligenceTip, Long> {

    @Query("""
        SELECT t FROM IntelligenceTip t
        WHERE (:status IS NULL OR t.status = :status)
          AND (:category IS NULL OR t.category = :category)
        ORDER BY t.createdAt DESC
        """)
    Page<IntelligenceTip> search(@Param("status") TipStatus status,
                                  @Param("category") com.antidoping.intelligence.entity.TipCategory category,
                                  Pageable pageable);

    List<IntelligenceTip> findByAthleteIdOrderByCreatedAtDesc(Long athleteId);

    long countByStatus(TipStatus status);

    long countByAthleteIdAndStatusIn(Long athleteId, List<TipStatus> statuses);

    long countByCategory(com.antidoping.intelligence.entity.TipCategory category);
}
