package com.evizy.evizy.service;

import com.evizy.evizy.constant.ResponseMessage;
import com.evizy.evizy.domain.dao.*;
import com.evizy.evizy.domain.dto.*;
import com.evizy.evizy.errors.BusinessFlowException;
import com.evizy.evizy.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class HealthFacilitiesVaccinesService {
    private final HealthFacilitiesVaccinesRepository healthFacilitiesVaccinesRepository;
    private final HealthFacilityRepository healthFacilityRepository;
    private final VaccinationSessionsRepository vaccinationSessionsRepository;
    private final VaccineRepository vaccineRepository;
    private final VaccineDistributionRepository vaccineDistributionRepository;

    public List<HealthFacilityVaccinesRequest> find(Long healthFacilityId) {
        List<HealthFacilitiesVaccines> healthFacilitiesVaccinesList = healthFacilitiesVaccinesRepository.findAllByHealthFacilityId(healthFacilityId);
        List<HealthFacilityVaccinesRequest> healthFacilityVaccinesRequests = new ArrayList<>();
        for(HealthFacilitiesVaccines healthFacilitiesVaccines : healthFacilitiesVaccinesList) {
            Vaccine vaccine = healthFacilitiesVaccines.getVaccine();
            HealthFacility healthFacility = healthFacilitiesVaccines.getHealthFacility();

            healthFacilityVaccinesRequests.add(HealthFacilityVaccinesRequest
                    .builder()
                    .healthFacility(HealthFacilityRequest.builder()
                            .id(healthFacility.getId())
                            .name(healthFacility.getName())
                            .build())
                    .vaccine(VaccineRequest.builder()
                            .id(vaccine.getId())
                            .name(vaccine.getName())
                            .build())
                    .stock(healthFacilitiesVaccines.getStock())
                    .build());
        }
        return healthFacilityVaccinesRequests;
    }

    public HealthFacilityVaccinesRequest distribute(VaccineDistributionRequest request) {
        Optional<HealthFacility> optionalHealthFacility = healthFacilityRepository.findById(request.getHealthFacility().getId());
        if (optionalHealthFacility.isEmpty()) {
            throw new BusinessFlowException(HttpStatus.BAD_REQUEST, ResponseMessage.BAD_REQUEST, "Health facility not found!");
        }

        Optional<Vaccine> optionalVaccine = vaccineRepository.findById(request.getVaccine().getId());
        if (optionalVaccine.isEmpty()) {
            throw new BusinessFlowException(HttpStatus.BAD_REQUEST, ResponseMessage.BAD_REQUEST, "Vaccine not found!");
        }

        VaccinationSessions vaccinationSessions = null;

        if (request.getVaccinationSession() != null && request.getVaccinationSession().getId() != null) {
            Optional<VaccinationSessions> optionalVaccinationSessions = vaccinationSessionsRepository.findById(request.getVaccinationSession().getId());
            if (optionalVaccinationSessions.isEmpty()) {
                throw new BusinessFlowException(HttpStatus.BAD_REQUEST, ResponseMessage.BAD_REQUEST, "Vaccination session not found!");
            }

            vaccinationSessions = VaccinationSessions.builder()
                    .id(request.getVaccinationSession().getId())
                    .build();
        }

        HealthFacilitiesVaccines.HealthFacilitiesVaccinesId id = new HealthFacilitiesVaccines.HealthFacilitiesVaccinesId(
                request.getHealthFacility().getId(),
                request.getVaccine().getId()
        );

        Optional<HealthFacilitiesVaccines> optionalHealthFacilitiesVaccines = healthFacilitiesVaccinesRepository.findById(id);
        HealthFacilitiesVaccines healthFacilitiesVaccines;
        if (optionalHealthFacilitiesVaccines.isEmpty()) {
            healthFacilitiesVaccines = HealthFacilitiesVaccines
                    .builder()
                    .healthFacility(HealthFacility.builder()
                            .id(request.getHealthFacility().getId())
                            .build())
                    .vaccine(Vaccine.builder()
                            .id(request.getVaccine().getId())
                            .build())
                    .stock(0L)
                    .build();
            healthFacilitiesVaccinesRepository.save(healthFacilitiesVaccines);
        } else {
            healthFacilitiesVaccines = optionalHealthFacilitiesVaccines.get();
        }

        if (healthFacilitiesVaccines.getStock() + request.getQuantity() < 0) {
            throw new BusinessFlowException(HttpStatus.BAD_REQUEST, ResponseMessage.STOCK_NOT_VALID, "Stock is not enough!");
        }

        healthFacilitiesVaccines.setStock(healthFacilitiesVaccines.getStock() + request.getQuantity());
        healthFacilitiesVaccinesRepository.save(healthFacilitiesVaccines);

        VaccineDistribution vaccineDistribution = VaccineDistribution.builder()
                .healthFacility(healthFacilitiesVaccines.getHealthFacility())
                .vaccine(healthFacilitiesVaccines.getVaccine())
                .vaccinationSession(vaccinationSessions)
                .quantity(request.getQuantity())
                .build();
        vaccineDistributionRepository.save(vaccineDistribution);

        return HealthFacilityVaccinesRequest.builder()
                .healthFacility(HealthFacilityRequest.builder()
                        .id(healthFacilitiesVaccines.getHealthFacility().getId())
                        .name(healthFacilitiesVaccines.getHealthFacility().getName())
                        .build())
                .stock(healthFacilitiesVaccines.getStock())
                .vaccine(VaccineRequest.builder()
                        .id(healthFacilitiesVaccines.getVaccine().getId())
                        .name(healthFacilitiesVaccines.getVaccine().getName())
                        .build())
                .build();
    }
}
