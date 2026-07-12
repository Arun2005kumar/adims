package com.antidoping.intelligence.service;

import com.antidoping.intelligence.dto.PageResponse;
import com.antidoping.intelligence.dto.request.AthleteRequest;
import com.antidoping.intelligence.dto.response.AthleteResponse;
import com.antidoping.intelligence.entity.AthleteStatus;
import com.antidoping.intelligence.entity.RiskLevel;
import org.springframework.data.domain.Pageable;

public interface AthleteService {
    PageResponse<AthleteResponse> search(String sport, AthleteStatus status, RiskLevel riskLevel, String query, Pageable pageable);
    AthleteResponse getById(Long id);
    AthleteResponse create(AthleteRequest request);
    AthleteResponse update(Long id, AthleteRequest request);
    void delete(Long id);
}
