package com.antidoping.intelligence.service.impl;

import com.antidoping.intelligence.dto.PageResponse;
import com.antidoping.intelligence.dto.request.TipRequest;
import com.antidoping.intelligence.dto.response.CaseResponse;
import com.antidoping.intelligence.dto.response.TipResponse;
import com.antidoping.intelligence.entity.*;
import com.antidoping.intelligence.exception.BadRequestException;
import com.antidoping.intelligence.exception.ResourceNotFoundException;
import com.antidoping.intelligence.repository.AthleteRepository;
import com.antidoping.intelligence.repository.IntelligenceTipRepository;
import com.antidoping.intelligence.repository.InvestigationCaseRepository;
import com.antidoping.intelligence.repository.UserRepository;
import com.antidoping.intelligence.service.AuditLogService;
import com.antidoping.intelligence.service.TipService;
import com.antidoping.intelligence.util.CaseNumberGenerator;
import com.antidoping.intelligence.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TipServiceImpl implements TipService {

    private final IntelligenceTipRepository tipRepository;
    private final AthleteRepository athleteRepository;
    private final UserRepository userRepository;
    private final InvestigationCaseRepository caseRepository;
    private final AuditLogService auditLogService;

    @Override
    public PageResponse<TipResponse> search(TipStatus status, TipCategory category, Pageable pageable) {
        Page<IntelligenceTip> page = tipRepository.search(status, category, pageable);
        return PageResponse.from(page.map(this::toResponse));
    }

    @Override
    public TipResponse getById(Long id) {
        return toResponse(findTip(id));
    }

    @Override
    @Transactional
    public TipResponse submit(TipRequest request, boolean anonymous) {
        Athlete athlete = null;
        if (request.getAthleteId() != null) {
            athlete = athleteRepository.findById(request.getAthleteId())
                    .orElseThrow(() -> new ResourceNotFoundException("Athlete not found with id: " + request.getAthleteId()));
        }

        User submittedBy = null;
        if (!anonymous) {
            Long userId = SecurityUtils.getCurrentUserId();
            if (userId != null) {
                submittedBy = userRepository.findById(userId).orElse(null);
            }
        }

        IntelligenceTip tip = IntelligenceTip.builder()
                .sourceType(request.getSourceType())
                .sourceContact(request.getSourceContact())
                .athlete(athlete)
                .category(request.getCategory())
                .description(request.getDescription())
                .credibilityScore(request.getCredibilityScore() != null ? request.getCredibilityScore() : 1)
                .status(TipStatus.NEW)
                .submittedBy(submittedBy)
                .build();

        IntelligenceTip saved = tipRepository.save(tip);
        auditLogService.log("SUBMIT_TIP", "IntelligenceTip", saved.getId(),
                "New intelligence tip submitted (category: " + saved.getCategory() + ")");
        return toResponse(saved);
    }

    @Override
    @Transactional
    public TipResponse updateStatus(Long id, TipStatus status) {
        IntelligenceTip tip = findTip(id);
        tip.setStatus(status);
        IntelligenceTip saved = tipRepository.save(tip);
        auditLogService.log("UPDATE_TIP_STATUS", "IntelligenceTip", id, "Status changed to " + status);
        return toResponse(saved);
    }

    @Override
    @Transactional
    public CaseResponse escalateToCase(Long tipId) {
        IntelligenceTip tip = findTip(tipId);

        if (tip.getStatus() == TipStatus.ESCALATED) {
            throw new BadRequestException("Tip has already been escalated to a case");
        }

        long sequence = caseRepository.count() + 1;
        String caseNumber = CaseNumberGenerator.generate(sequence);
        while (caseRepository.existsByCaseNumber(caseNumber)) {
            sequence++;
            caseNumber = CaseNumberGenerator.generate(sequence);
        }

        InvestigationCase investigationCase = InvestigationCase.builder()
                .caseNumber(caseNumber)
                .title("Investigation from tip #" + tip.getId() + " - " + tip.getCategory())
                .athlete(tip.getAthlete())
                .tip(tip)
                .priority(mapCredibilityToPriority(tip.getCredibilityScore()))
                .status(CaseStatus.OPEN)
                .description(tip.getDescription())
                .openedDate(LocalDate.now())
                .build();

        InvestigationCase savedCase = caseRepository.save(investigationCase);

        tip.setStatus(TipStatus.ESCALATED);
        tipRepository.save(tip);

        auditLogService.log("ESCALATE_TIP", "IntelligenceTip", tipId,
                "Escalated to case " + savedCase.getCaseNumber());

        return CaseResponse.builder()
                .id(savedCase.getId())
                .caseNumber(savedCase.getCaseNumber())
                .title(savedCase.getTitle())
                .athleteId(savedCase.getAthlete() != null ? savedCase.getAthlete().getId() : null)
                .athleteName(savedCase.getAthlete() != null
                        ? savedCase.getAthlete().getFirstName() + " " + savedCase.getAthlete().getLastName() : null)
                .tipId(tip.getId())
                .priority(savedCase.getPriority().name())
                .status(savedCase.getStatus().name())
                .description(savedCase.getDescription())
                .openedDate(savedCase.getOpenedDate())
                .createdAt(savedCase.getCreatedAt())
                .updatedAt(savedCase.getUpdatedAt())
                .build();
    }

    @Override
    public List<TipResponse> getByAthlete(Long athleteId) {
        return tipRepository.findByAthleteIdOrderByCreatedAtDesc(athleteId).stream().map(this::toResponse).toList();
    }

    private CasePriority mapCredibilityToPriority(int credibilityScore) {
        if (credibilityScore >= 5) return CasePriority.CRITICAL;
        if (credibilityScore >= 4) return CasePriority.HIGH;
        if (credibilityScore >= 2) return CasePriority.MEDIUM;
        return CasePriority.LOW;
    }

    private IntelligenceTip findTip(Long id) {
        return tipRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Intelligence tip not found with id: " + id));
    }

    private TipResponse toResponse(IntelligenceTip tip) {
        return TipResponse.builder()
                .id(tip.getId())
                .sourceType(tip.getSourceType().name())
                .sourceContact(tip.getSourceContact())
                .athleteId(tip.getAthlete() != null ? tip.getAthlete().getId() : null)
                .athleteName(tip.getAthlete() != null
                        ? tip.getAthlete().getFirstName() + " " + tip.getAthlete().getLastName() : null)
                .category(tip.getCategory().name())
                .description(tip.getDescription())
                .credibilityScore(tip.getCredibilityScore())
                .status(tip.getStatus().name())
                .submittedByUsername(tip.getSubmittedBy() != null ? tip.getSubmittedBy().getUsername() : "Anonymous")
                .createdAt(tip.getCreatedAt())
                .updatedAt(tip.getUpdatedAt())
                .build();
    }
}
