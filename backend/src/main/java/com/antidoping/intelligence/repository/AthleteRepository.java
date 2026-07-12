package com.antidoping.intelligence.repository;

import com.antidoping.intelligence.entity.Athlete;
import com.antidoping.intelligence.entity.AthleteStatus;
import com.antidoping.intelligence.entity.RiskLevel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AthleteRepository extends JpaRepository<Athlete, Long> {

    @Query("""
        SELECT a FROM Athlete a
        WHERE (:sport IS NULL OR LOWER(a.sport) = LOWER(:sport))
          AND (:status IS NULL OR a.status = :status)
          AND (:riskLevel IS NULL OR a.riskLevel = :riskLevel)
          AND (:search IS NULL OR LOWER(a.firstName) LIKE LOWER(CONCAT('%', :search, '%'))
               OR LOWER(a.lastName) LIKE LOWER(CONCAT('%', :search, '%')))
        """)
    Page<Athlete> search(@Param("sport") String sport,
                          @Param("status") AthleteStatus status,
                          @Param("riskLevel") RiskLevel riskLevel,
                          @Param("search") String search,
                          Pageable pageable);

    long countByRiskLevel(RiskLevel riskLevel);
    long countByStatus(AthleteStatus status);
}
