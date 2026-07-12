package com.antidoping.intelligence.service;

import com.antidoping.intelligence.dto.response.RiskAssessmentResponse;

import java.util.List;

public interface RiskAssessmentService {
    List<RiskAssessmentResponse> getHistory(Long athleteId);
    RiskAssessmentResponse recalculate(Long athleteId);
}
