package com.antidoping.intelligence.service;

import com.antidoping.intelligence.dto.PageResponse;
import com.antidoping.intelligence.dto.request.TestingRecordRequest;
import com.antidoping.intelligence.dto.response.TestingRecordResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TestingRecordService {
    List<TestingRecordResponse> getByAthlete(Long athleteId);
    PageResponse<TestingRecordResponse> getAll(Pageable pageable);
    TestingRecordResponse create(TestingRecordRequest request);
}
