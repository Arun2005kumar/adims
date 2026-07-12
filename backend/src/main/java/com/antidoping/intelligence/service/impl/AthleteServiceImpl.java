package com.antidoping.intelligence.service.impl;

import com.antidoping.intelligence.dto.PageResponse;
import com.antidoping.intelligence.dto.request.AthleteRequest;
import com.antidoping.intelligence.dto.response.AthleteResponse;
import com.antidoping.intelligence.entity.Athlete;
import com.antidoping.intelligence.entity.AthleteStatus;
import com.antidoping.intelligence.entity.RiskLevel;
import com.antidoping.intelligence.exception.ResourceNotFoundException;
import com.antidoping.intelligence.repository.AthleteRepository;
import com.antidoping.intelligence.service.AthleteService;
import com.antidoping.intelligence.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AthleteServiceImpl implements AthleteService {

    private final AthleteRepository athleteRepository;
    private final AuditLogService auditLogService;

    @Override
    public PageResponse<AthleteResponse> search(String sport, AthleteStatus status, RiskLevel riskLevel, String query, Pageable pageable) {
        Page<Athlete> page = athleteRepository.search(
                blankToNull(sport), status, riskLevel, blankToNull(query), pageable);
        return PageResponse.from(page.map(this::toResponse));
    }

    @Override
    public AthleteResponse getById(Long id) {
        return toResponse(findAthlete(id));
    }

    @Override
    @Transactional
    public AthleteResponse create(AthleteRequest request) {
        Athlete athlete = Athlete.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .dateOfBirth(request.getDateOfBirth())
                .gender(request.getGender())
                .nationality(request.getNationality())
                .sport(request.getSport())
                .discipline(request.getDiscipline())
                .testingPool(request.getTestingPool())
                .status(request.getStatus() != null ? request.getStatus() : AthleteStatus.ACTIVE)
                .riskLevel(RiskLevel.LOW)
                .build();

        Athlete saved = athleteRepository.save(athlete);
        auditLogService.log("CREATE_ATHLETE", "Athlete", saved.getId(),
                "Created athlete profile: " + saved.getFirstName() + " " + saved.getLastName());
        return toResponse(saved);
    }

    @Override
    @Transactional
    public AthleteResponse update(Long id, AthleteRequest request) {
        Athlete athlete = findAthlete(id);
        athlete.setFirstName(request.getFirstName());
        athlete.setLastName(request.getLastName());
        athlete.setDateOfBirth(request.getDateOfBirth());
        athlete.setGender(request.getGender());
        athlete.setNationality(request.getNationality());
        athlete.setSport(request.getSport());
        athlete.setDiscipline(request.getDiscipline());
        athlete.setTestingPool(request.getTestingPool());
        if (request.getStatus() != null) {
            athlete.setStatus(request.getStatus());
        }

        Athlete saved = athleteRepository.save(athlete);
        auditLogService.log("UPDATE_ATHLETE", "Athlete", id, "Updated athlete profile");
        return toResponse(saved);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Athlete athlete = findAthlete(id);
        athleteRepository.delete(athlete);
        auditLogService.log("DELETE_ATHLETE", "Athlete", id, "Deleted athlete profile");
    }

    private Athlete findAthlete(Long id) {
        return athleteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Athlete not found with id: " + id));
    }

    private String blankToNull(String value) {
        return (value == null || value.isBlank()) ? null : value;
    }

    private AthleteResponse toResponse(Athlete a) {
        return AthleteResponse.builder()
                .id(a.getId())
                .firstName(a.getFirstName())
                .lastName(a.getLastName())
                .dateOfBirth(a.getDateOfBirth())
                .gender(a.getGender())
                .nationality(a.getNationality())
                .sport(a.getSport())
                .discipline(a.getDiscipline())
                .testingPool(a.getTestingPool().name())
                .status(a.getStatus().name())
                .riskLevel(a.getRiskLevel().name())
                .createdAt(a.getCreatedAt())
                .updatedAt(a.getUpdatedAt())
                .build();
    }
}
