package com.antidoping.intelligence.service.impl;

import com.antidoping.intelligence.dto.PageResponse;
import com.antidoping.intelligence.dto.request.*;
import com.antidoping.intelligence.dto.response.*;
import com.antidoping.intelligence.entity.*;
import com.antidoping.intelligence.exception.ResourceNotFoundException;
import com.antidoping.intelligence.repository.*;
import com.antidoping.intelligence.service.AuditLogService;
import com.antidoping.intelligence.service.CaseService;
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
public class CaseServiceImpl implements CaseService {

    private final InvestigationCaseRepository caseRepository;
    private final AthleteRepository athleteRepository;
    private final IntelligenceTipRepository tipRepository;
    private final UserRepository userRepository;
    private final CaseNoteRepository caseNoteRepository;
    private final EvidenceRepository evidenceRepository;
    private final AuditLogService auditLogService;

    @Override
    public PageResponse<CaseResponse> search(CaseStatus status, CasePriority priority, Long assignedTo, Pageable pageable) {
        Page<InvestigationCase> page = caseRepository.search(status, priority, assignedTo, pageable);
        return PageResponse.from(page.map(this::toResponse));
    }

    @Override
    public CaseResponse getById(Long id) {
        return toResponse(findCase(id));
    }

    @Override
    @Transactional
    public CaseResponse create(CaseRequest request) {
        Athlete athlete = null;
        if (request.getAthleteId() != null) {
            athlete = athleteRepository.findById(request.getAthleteId())
                    .orElseThrow(() -> new ResourceNotFoundException("Athlete not found with id: " + request.getAthleteId()));
        }

        IntelligenceTip tip = null;
        if (request.getTipId() != null) {
            tip = tipRepository.findById(request.getTipId())
                    .orElseThrow(() -> new ResourceNotFoundException("Tip not found with id: " + request.getTipId()));
        }

        User investigator = null;
        if (request.getAssignedInvestigatorId() != null) {
            investigator = userRepository.findById(request.getAssignedInvestigatorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Investigator not found with id: " + request.getAssignedInvestigatorId()));
        }

        long sequence = caseRepository.count() + 1;
        String caseNumber = CaseNumberGenerator.generate(sequence);
        while (caseRepository.existsByCaseNumber(caseNumber)) {
            sequence++;
            caseNumber = CaseNumberGenerator.generate(sequence);
        }

        InvestigationCase investigationCase = InvestigationCase.builder()
                .caseNumber(caseNumber)
                .title(request.getTitle())
                .athlete(athlete)
                .tip(tip)
                .priority(request.getPriority() != null ? request.getPriority() : CasePriority.MEDIUM)
                .status(CaseStatus.OPEN)
                .assignedInvestigator(investigator)
                .description(request.getDescription())
                .openedDate(LocalDate.now())
                .build();

        InvestigationCase saved = caseRepository.save(investigationCase);
        auditLogService.log("CREATE_CASE", "InvestigationCase", saved.getId(), "Opened case " + saved.getCaseNumber());
        return toResponse(saved);
    }

    @Override
    @Transactional
    public CaseResponse updateStatus(Long id, CaseStatusUpdateRequest request) {
        InvestigationCase investigationCase = findCase(id);
        investigationCase.setStatus(request.getStatus());
        if (request.getOutcome() != null) {
            investigationCase.setOutcome(request.getOutcome());
        }
        if (request.getStatus() == CaseStatus.CLOSED || request.getStatus() == CaseStatus.ARCHIVED) {
            investigationCase.setClosedDate(LocalDate.now());
        }
        InvestigationCase saved = caseRepository.save(investigationCase);
        auditLogService.log("UPDATE_CASE_STATUS", "InvestigationCase", id, "Status changed to " + request.getStatus());
        return toResponse(saved);
    }

    @Override
    @Transactional
    public CaseResponse assign(Long id, CaseAssignRequest request) {
        InvestigationCase investigationCase = findCase(id);
        User investigator = userRepository.findById(request.getInvestigatorId())
                .orElseThrow(() -> new ResourceNotFoundException("Investigator not found with id: " + request.getInvestigatorId()));
        investigationCase.setAssignedInvestigator(investigator);
        if (investigationCase.getStatus() == CaseStatus.OPEN) {
            investigationCase.setStatus(CaseStatus.IN_PROGRESS);
        }
        InvestigationCase saved = caseRepository.save(investigationCase);
        auditLogService.log("ASSIGN_CASE", "InvestigationCase", id, "Assigned to " + investigator.getUsername());
        return toResponse(saved);
    }

    @Override
    public List<CaseResponse> getByAthlete(Long athleteId) {
        return caseRepository.findByAthleteIdOrderByCreatedAtDesc(athleteId).stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional
    public CaseNoteResponse addNote(Long caseId, CaseNoteRequest request) {
        InvestigationCase investigationCase = findCase(caseId);

        Long userId = SecurityUtils.getCurrentUserId();
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Current user not found"));

        CaseNote note = CaseNote.builder()
                .investigationCase(investigationCase)
                .author(author)
                .note(request.getNote())
                .build();

        CaseNote saved = caseNoteRepository.save(note);
        auditLogService.log("ADD_CASE_NOTE", "InvestigationCase", caseId, "Note added by " + author.getUsername());
        return toNoteResponse(saved);
    }

    @Override
    public List<CaseNoteResponse> getNotes(Long caseId) {
        return caseNoteRepository.findByInvestigationCaseIdOrderByCreatedAtDesc(caseId).stream()
                .map(this::toNoteResponse).toList();
    }

    @Override
    @Transactional
    public EvidenceResponse addEvidence(Long caseId, EvidenceRequest request) {
        InvestigationCase investigationCase = findCase(caseId);

        User collectedBy = null;
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId != null) {
            collectedBy = userRepository.findById(userId).orElse(null);
        }

        Evidence evidence = Evidence.builder()
                .investigationCase(investigationCase)
                .type(request.getType())
                .description(request.getDescription())
                .fileReference(request.getFileReference())
                .collectedBy(collectedBy)
                .collectedDate(request.getCollectedDate() != null ? request.getCollectedDate() : LocalDate.now())
                .build();

        Evidence saved = evidenceRepository.save(evidence);
        auditLogService.log("ADD_EVIDENCE", "InvestigationCase", caseId, "Evidence added: " + request.getType());
        return toEvidenceResponse(saved);
    }

    @Override
    public List<EvidenceResponse> getEvidence(Long caseId) {
        return evidenceRepository.findByInvestigationCaseIdOrderByCollectedDateDesc(caseId).stream()
                .map(this::toEvidenceResponse).toList();
    }

    private InvestigationCase findCase(Long id) {
        return caseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Case not found with id: " + id));
    }

    private CaseResponse toResponse(InvestigationCase c) {
        return CaseResponse.builder()
                .id(c.getId())
                .caseNumber(c.getCaseNumber())
                .title(c.getTitle())
                .athleteId(c.getAthlete() != null ? c.getAthlete().getId() : null)
                .athleteName(c.getAthlete() != null ? c.getAthlete().getFirstName() + " " + c.getAthlete().getLastName() : null)
                .tipId(c.getTip() != null ? c.getTip().getId() : null)
                .priority(c.getPriority().name())
                .status(c.getStatus().name())
                .assignedInvestigatorId(c.getAssignedInvestigator() != null ? c.getAssignedInvestigator().getId() : null)
                .assignedInvestigatorName(c.getAssignedInvestigator() != null ? c.getAssignedInvestigator().getFullName() : null)
                .description(c.getDescription())
                .openedDate(c.getOpenedDate())
                .closedDate(c.getClosedDate())
                .outcome(c.getOutcome())
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt())
                .build();
    }

    private CaseNoteResponse toNoteResponse(CaseNote note) {
        return CaseNoteResponse.builder()
                .id(note.getId())
                .caseId(note.getInvestigationCase().getId())
                .authorUsername(note.getAuthor().getUsername())
                .note(note.getNote())
                .createdAt(note.getCreatedAt())
                .build();
    }

    private EvidenceResponse toEvidenceResponse(Evidence e) {
        return EvidenceResponse.builder()
                .id(e.getId())
                .caseId(e.getInvestigationCase().getId())
                .type(e.getType().name())
                .description(e.getDescription())
                .fileReference(e.getFileReference())
                .collectedByUsername(e.getCollectedBy() != null ? e.getCollectedBy().getUsername() : null)
                .collectedDate(e.getCollectedDate())
                .createdAt(e.getCreatedAt())
                .build();
    }
}
