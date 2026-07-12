package com.antidoping.intelligence.service.impl;

import com.antidoping.intelligence.dto.PageResponse;
import com.antidoping.intelligence.dto.request.TestingRecordRequest;
import com.antidoping.intelligence.dto.response.TestingRecordResponse;
import com.antidoping.intelligence.entity.Athlete;
import com.antidoping.intelligence.entity.TestResult;
import com.antidoping.intelligence.entity.TestingRecord;
import com.antidoping.intelligence.exception.ResourceNotFoundException;
import com.antidoping.intelligence.repository.AthleteRepository;
import com.antidoping.intelligence.repository.TestingRecordRepository;
import com.antidoping.intelligence.service.AuditLogService;
import com.antidoping.intelligence.service.TestingRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TestingRecordServiceImpl implements TestingRecordService {

    private final TestingRecordRepository testingRecordRepository;
    private final AthleteRepository athleteRepository;
    private final AuditLogService auditLogService;

    @Override
    public List<TestingRecordResponse> getByAthlete(Long athleteId) {
        return testingRecordRepository.findByAthleteIdOrderByTestDateDesc(athleteId).stream()
                .map(this::toResponse).toList();
    }

    @Override
    public PageResponse<TestingRecordResponse> getAll(Pageable pageable) {
        Page<TestingRecord> page = testingRecordRepository.findAllByOrderByTestDateDesc(pageable);
        return PageResponse.from(page.map(this::toResponse));
    }

    @Override
    @Transactional
    public TestingRecordResponse create(TestingRecordRequest request) {
        Athlete athlete = athleteRepository.findById(request.getAthleteId())
                .orElseThrow(() -> new ResourceNotFoundException("Athlete not found with id: " + request.getAthleteId()));

        TestingRecord record = TestingRecord.builder()
                .athlete(athlete)
                .testDate(request.getTestDate())
                .testType(request.getTestType())
                .result(request.getResult() != null ? request.getResult() : TestResult.PENDING)
                .sampleId(request.getSampleId())
                .labName(request.getLabName())
                .notes(request.getNotes())
                .build();

        TestingRecord saved = testingRecordRepository.save(record);
        auditLogService.log("CREATE_TESTING_RECORD", "Athlete", athlete.getId(),
                "Testing record added: " + saved.getResult());
        return toResponse(saved);
    }

    private TestingRecordResponse toResponse(TestingRecord r) {
        return TestingRecordResponse.builder()
                .id(r.getId())
                .athleteId(r.getAthlete().getId())
                .athleteName(r.getAthlete().getFirstName() + " " + r.getAthlete().getLastName())
                .testDate(r.getTestDate())
                .testType(r.getTestType().name())
                .result(r.getResult().name())
                .sampleId(r.getSampleId())
                .labName(r.getLabName())
                .notes(r.getNotes())
                .build();
    }
}
