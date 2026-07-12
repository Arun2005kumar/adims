package com.antidoping.intelligence.controller;

import com.antidoping.intelligence.dto.PageResponse;
import com.antidoping.intelligence.dto.request.TestingRecordRequest;
import com.antidoping.intelligence.dto.response.TestingRecordResponse;
import com.antidoping.intelligence.service.TestingRecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/testing-records")
@RequiredArgsConstructor
public class TestingRecordController {

    private final TestingRecordService testingRecordService;

    @GetMapping
    public ResponseEntity<PageResponse<TestingRecordResponse>> getAll(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(testingRecordService.getAll(pageable));
    }

    @GetMapping("/athlete/{athleteId}")
    public ResponseEntity<List<TestingRecordResponse>> getByAthlete(@PathVariable Long athleteId) {
        return ResponseEntity.ok(testingRecordService.getByAthlete(athleteId));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','INVESTIGATOR','ANALYST')")
    public ResponseEntity<TestingRecordResponse> create(@Valid @RequestBody TestingRecordRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(testingRecordService.create(request));
    }
}
