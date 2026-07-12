package com.antidoping.intelligence.controller;

import com.antidoping.intelligence.dto.request.TipRequest;
import com.antidoping.intelligence.dto.response.TipResponse;
import com.antidoping.intelligence.service.TipService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoints reachable without authentication - primarily for anonymous
 * informants submitting intelligence tips from the public-facing tip line.
 */
@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class PublicController {

    private final TipService tipService;

    @PostMapping("/tips")
    public ResponseEntity<TipResponse> submitAnonymousTip(@Valid @RequestBody TipRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tipService.submit(request, true));
    }
}
