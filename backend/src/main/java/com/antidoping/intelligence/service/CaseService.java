package com.antidoping.intelligence.service;

import com.antidoping.intelligence.dto.PageResponse;
import com.antidoping.intelligence.dto.request.*;
import com.antidoping.intelligence.dto.response.*;
import com.antidoping.intelligence.entity.CasePriority;
import com.antidoping.intelligence.entity.CaseStatus;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CaseService {
    PageResponse<CaseResponse> search(CaseStatus status, CasePriority priority, Long assignedTo, Pageable pageable);
    CaseResponse getById(Long id);
    CaseResponse create(CaseRequest request);
    CaseResponse updateStatus(Long id, CaseStatusUpdateRequest request);
    CaseResponse assign(Long id, CaseAssignRequest request);
    List<CaseResponse> getByAthlete(Long athleteId);

    CaseNoteResponse addNote(Long caseId, CaseNoteRequest request);
    List<CaseNoteResponse> getNotes(Long caseId);

    EvidenceResponse addEvidence(Long caseId, EvidenceRequest request);
    List<EvidenceResponse> getEvidence(Long caseId);
}
