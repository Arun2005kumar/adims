package com.antidoping.intelligence.service.impl;

import com.antidoping.intelligence.dto.response.RiskAssessmentResponse;
import com.antidoping.intelligence.entity.*;
import com.antidoping.intelligence.exception.ResourceNotFoundException;
import com.antidoping.intelligence.repository.*;
import com.antidoping.intelligence.service.AuditLogService;
import com.antidoping.intelligence.service.RiskAssessmentService;
import com.antidoping.intelligence.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RiskAssessmentServiceImpl implements RiskAssessmentService {

    private final RiskAssessmentRepository riskAssessmentRepository;
    private final AthleteRepository athleteRepository;
    private final UserRepository userRepository;
    private final IntelligenceTipRepository tipRepository;
    private final InvestigationCaseRepository caseRepository;
    private final TestingRecordRepository testingRecordRepository;
    private final AuditLogService auditLogService;

    @Override
    public List<RiskAssessmentResponse> getHistory(Long athleteId) {
        return riskAssessmentRepository.findByAthleteIdOrderByAssessmentDateDesc(athleteId).stream()
                .map(this::toResponse).toList();
    }

    /**
     * Recalculates an athlete's risk score using a weighted, transparent formula based on:
     *  - number of open/under-review intelligence tips
     *  - number of open investigation cases
     *  - number of missed / atypical / positive test results (proxy: non-negative results)
     *  - prior violations (persisted from the last assessment, since this is historical and doesn't reset)
     *
     * This is a rule-based heuristic suitable for demonstration purposes; a production system
     * would refine weightings using historical outcome data.
     */
    @Override
    @Transactional
    public RiskAssessmentResponse recalculate(Long athleteId) {
        Athlete athlete = athleteRepository.findById(athleteId)
                .orElseThrow(() -> new ResourceNotFoundException("Athlete not found with id: " + athleteId));

        long openTips = tipRepository.countByAthleteIdAndStatusIn(athleteId,
                List.of(TipStatus.NEW, TipStatus.UNDER_REVIEW, TipStatus.ESCALATED));

        long openCases = caseRepository.countByAthleteIdAndStatusIn(athleteId,
                List.of(CaseStatus.OPEN, CaseStatus.IN_PROGRESS, CaseStatus.PENDING_REVIEW));

        long atypicalOrPositive = testingRecordRepository.countByAthleteIdAndResult(athleteId, TestResult.ATYPICAL)
                + testingRecordRepository.countByAthleteIdAndResult(athleteId, TestResult.POSITIVE);

        int priorViolations = riskAssessmentRepository.findFirstByAthleteIdOrderByAssessmentDateDesc(athleteId)
                .map(RiskAssessment::getPriorViolationsCount).orElse(0);

        // Weighted scoring: tips(10), cases(20), adverse tests(25), prior violations(15) - capped at 100
        double score = (openTips * 10.0) + (openCases * 20.0) + (atypicalOrPositive * 25.0) + (priorViolations * 15.0);
        score = Math.min(score, 100.0);

        RiskLevel level;
        if (score >= 70) level = RiskLevel.CRITICAL;
        else if (score >= 45) level = RiskLevel.HIGH;
        else if (score >= 20) level = RiskLevel.MEDIUM;
        else level = RiskLevel.LOW;

        Long currentUserId = SecurityUtils.getCurrentUserId();
        var assessor = currentUserId != null ? userRepository.findById(currentUserId).orElse(null) : null;

        RiskAssessment assessment = RiskAssessment.builder()
                .athlete(athlete)
                .assessmentDate(LocalDate.now())
                .missedTestsCount(0)
                .priorViolationsCount(priorViolations)
                .openTipsCount((int) openTips)
                .openCasesCount((int) openCases)
                .riskScore(BigDecimal.valueOf(score).setScale(2, RoundingMode.HALF_UP))
                .riskLevel(level)
                .assessedBy(assessor)
                .notes("Auto-recalculated based on current open tips/cases and testing history.")
                .build();

        RiskAssessment saved = riskAssessmentRepository.save(assessment);

        athlete.setRiskLevel(level);
        athleteRepository.save(athlete);

        auditLogService.log("RECALCULATE_RISK", "Athlete", athleteId,
                "Risk recalculated: score=" + assessment.getRiskScore() + " level=" + level);

        return toResponse(saved);
    }

    private RiskAssessmentResponse toResponse(RiskAssessment r) {
        return RiskAssessmentResponse.builder()
                .id(r.getId())
                .athleteId(r.getAthlete().getId())
                .athleteName(r.getAthlete().getFirstName() + " " + r.getAthlete().getLastName())
                .assessmentDate(r.getAssessmentDate())
                .missedTestsCount(r.getMissedTestsCount())
                .priorViolationsCount(r.getPriorViolationsCount())
                .openTipsCount(r.getOpenTipsCount())
                .openCasesCount(r.getOpenCasesCount())
                .riskScore(r.getRiskScore())
                .riskLevel(r.getRiskLevel().name())
                .assessedByUsername(r.getAssessedBy() != null ? r.getAssessedBy().getUsername() : "SYSTEM")
                .notes(r.getNotes())
                .build();
    }
}
