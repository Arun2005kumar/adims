package com.antidoping.intelligence.service;

import com.antidoping.intelligence.dto.PageResponse;
import com.antidoping.intelligence.dto.request.TipRequest;
import com.antidoping.intelligence.dto.response.CaseResponse;
import com.antidoping.intelligence.dto.response.TipResponse;
import com.antidoping.intelligence.entity.TipCategory;
import com.antidoping.intelligence.entity.TipStatus;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TipService {
    PageResponse<TipResponse> search(TipStatus status, TipCategory category, Pageable pageable);
    TipResponse getById(Long id);
    TipResponse submit(TipRequest request, boolean anonymous);
    TipResponse updateStatus(Long id, TipStatus status);
    CaseResponse escalateToCase(Long tipId);
    List<TipResponse> getByAthlete(Long athleteId);
}
