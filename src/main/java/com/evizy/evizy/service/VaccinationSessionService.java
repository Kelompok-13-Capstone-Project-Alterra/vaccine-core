package com.evizy.evizy.service;

import com.evizy.evizy.constant.ResponseMessage;
import com.evizy.evizy.domain.dao.HealthFacilitiesVaccines;
import com.evizy.evizy.domain.dao.HealthFacility;
import com.evizy.evizy.domain.dao.VaccinationSessions;
import com.evizy.evizy.domain.dao.Vaccine;
import com.evizy.evizy.domain.dto.HealthFacilityRequest;
import com.evizy.evizy.domain.dto.VaccinationSessionRequest;
import com.evizy.evizy.domain.dto.VaccineRequest;
import com.evizy.evizy.errors.BusinessFlowException;
import com.evizy.evizy.repository.HealthFacilitiesVaccinesRepository;
import com.evizy.evizy.repository.HealthFacilityRepository;
import com.evizy.evizy.repository.VaccinationSessionsRepository;
import com.evizy.evizy.repository.VaccineRepository;
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
public class VaccinationSessionService {
    private final VaccinationSessionsRepository vaccinationSessionsRepository;
    private final HealthFacilityRepository healthFacilityRepository;
    private final HealthFacilitiesVaccinesRepository healthFacilitiesVaccinesRepository;
    private final VaccineRepository vaccineRepository;

    public VaccinationSessionRequest create(VaccinationSessionRequest request) throws BusinessFlowException {
        Optional<HealthFacility> optionalHealthFacility = healthFacilityRepository.findById(request.getHealthFacility().getId());
        if (optionalHealthFacility.isEmpty()) {
            throw new BusinessFlowException(HttpStatus.BAD_REQUEST, ResponseMessage.NOT_FOUND, "Health facility not found!");
        }

        Optional<Vaccine> optionalVaccine = vaccineRepository.findById(request.getVaccine().getId());
        if (optionalVaccine.isEmpty()) {
            throw new BusinessFlowException(HttpStatus.BAD_REQUEST, ResponseMessage.NOT_FOUND, "Vaccine not found!");
        }

        HealthFacilitiesVaccines.HealthFacilitiesVaccinesId healthFacilitiesVaccinesId = new HealthFacilitiesVaccines.HealthFacilitiesVaccinesId(
                optionalHealthFacility.get().getId(),
                optionalVaccine.get().getId()
        );

        HealthFacilitiesVaccines healthFacilitiesVaccines;
        Optional<HealthFacilitiesVaccines> optionalHealthFacilitiesVaccines = healthFacilitiesVaccinesRepository.findById(healthFacilitiesVaccinesId);
        if (optionalHealthFacilitiesVaccines.isEmpty()) {
            healthFacilitiesVaccines = HealthFacilitiesVaccines.builder()
                    .stock(0L)
                    .vaccine(optionalVaccine.get())
                    .healthFacility(optionalHealthFacility.get())
                    .build();
            healthFacilitiesVaccinesRepository.save(healthFacilitiesVaccines);
        } else {
            healthFacilitiesVaccines = optionalHealthFacilitiesVaccines.get();
        }

        if (healthFacilitiesVaccines.getStock() < request.getQuantity()) {
            throw new BusinessFlowException(HttpStatus.BAD_REQUEST, ResponseMessage.STOCK_NOT_VALID, "Stock is not enough!");
        }

        healthFacilitiesVaccines.setStock(healthFacilitiesVaccines.getStock() - request.getQuantity());

        VaccinationSessions vaccinationSessions = VaccinationSessions.builder()
                .healthFacility(optionalHealthFacility.get())
                .vaccine(optionalVaccine.get())
                .scheduleStart(request.getScheduleStart())
                .scheduleEnd(request.getScheduleEnd())
                .quantity(request.getQuantity())
                .booked(0L)
                .build();
        vaccinationSessionsRepository.save(vaccinationSessions);
        healthFacilitiesVaccinesRepository.save(healthFacilitiesVaccines);
        return VaccinationSessionRequest.builder()
                .id(vaccinationSessions.getId())
                .healthFacility(HealthFacilityRequest.builder()
                        .id(optionalHealthFacility.get().getId())
                        .name(optionalHealthFacility.get().getName())
                        .build())
                .vaccine(VaccineRequest.builder()
                        .id(optionalVaccine.get().getId())
                        .name(optionalVaccine.get().getName())
                        .build())
                .scheduleStart(vaccinationSessions.getScheduleStart())
                .scheduleEnd(vaccinationSessions.getScheduleEnd())
                .quantity(vaccinationSessions.getQuantity())
                .booked(vaccinationSessions.getBooked())
                .build();
    }

    public void delete(Long id) throws BusinessFlowException {
        Optional<VaccinationSessions> optionalVaccinationSessions = vaccinationSessionsRepository.findById(id);
        if (optionalVaccinationSessions.isEmpty()) {
            throw new BusinessFlowException(HttpStatus.BAD_REQUEST, ResponseMessage.NOT_FOUND, "Vaccination session not found!");
        }

        if (optionalVaccinationSessions.get().getBooked() > 0) {
            throw new BusinessFlowException(HttpStatus.BAD_REQUEST, ResponseMessage.VACCINATION_SESSION_ALREADY_BOOKED, "Can't delete vaccination session because already booked!");
        }

        Vaccine vaccine = optionalVaccinationSessions.get().getVaccine();
        HealthFacility healthFacility = optionalVaccinationSessions.get().getHealthFacility();

        HealthFacilitiesVaccines.HealthFacilitiesVaccinesId healthFacilitiesVaccinesId = new HealthFacilitiesVaccines.HealthFacilitiesVaccinesId(
                healthFacility.getId(),
                vaccine.getId()
        );

        Optional<HealthFacilitiesVaccines> optionalHealthFacilitiesVaccines = healthFacilitiesVaccinesRepository.findById(healthFacilitiesVaccinesId);
        if (optionalHealthFacilitiesVaccines.isPresent()) {
            HealthFacilitiesVaccines healthFacilitiesVaccines = optionalHealthFacilitiesVaccines.get();
            healthFacilitiesVaccines.setStock(healthFacilitiesVaccines.getStock() + optionalVaccinationSessions.get().getQuantity());
            healthFacilitiesVaccinesRepository.save(healthFacilitiesVaccines);
        }

        vaccinationSessionsRepository.delete(optionalVaccinationSessions.get());
    }

    /**
     * Can't update the health facility of vaccination session
     */
    public VaccinationSessionRequest update(Long id, VaccinationSessionRequest request) throws BusinessFlowException {
        Optional<VaccinationSessions> optionalVaccinationSessions = vaccinationSessionsRepository.findById(id);
        if (optionalVaccinationSessions.isEmpty()) {
            throw new BusinessFlowException(HttpStatus.BAD_REQUEST, ResponseMessage.NOT_FOUND, "Vaccination session not found!");
        }

        VaccinationSessions vaccinationSessions = optionalVaccinationSessions.get();

        if (vaccinationSessions.getBooked() > 0) {
            throw new BusinessFlowException(HttpStatus.BAD_REQUEST, ResponseMessage.VACCINATION_SESSION_ALREADY_BOOKED, "Can't update vaccination session because already booked!");
        }

        Optional<Vaccine> optionalVaccine = vaccineRepository.findById(request.getVaccine().getId());
        if (optionalVaccine.isEmpty()) {
            throw new BusinessFlowException(HttpStatus.BAD_REQUEST, ResponseMessage.NOT_FOUND, "Vaccine not found!");
        }

        HealthFacilitiesVaccines.HealthFacilitiesVaccinesId requestedHealthFacilitiesVaccinesId = new HealthFacilitiesVaccines.HealthFacilitiesVaccinesId(
                vaccinationSessions.getHealthFacility().getId(),
                request.getVaccine().getId()
        );

        HealthFacilitiesVaccines.HealthFacilitiesVaccinesId healthFacilitiesVaccinesId = new HealthFacilitiesVaccines.HealthFacilitiesVaccinesId(
                vaccinationSessions.getHealthFacility().getId(),
                vaccinationSessions.getVaccine().getId()
        );

        HealthFacilitiesVaccines requestedHealthFacilitiesVaccines;

        Optional<HealthFacilitiesVaccines> optionalHealthFacilitiesVaccines = healthFacilitiesVaccinesRepository.findById(healthFacilitiesVaccinesId);
        HealthFacilitiesVaccines currentHealthFacilitiesVaccines = optionalHealthFacilitiesVaccines.get();
        Optional<HealthFacilitiesVaccines> optionalRequestedHealthFacilitiesVaccines = healthFacilitiesVaccinesRepository.findById(requestedHealthFacilitiesVaccinesId);
        if (optionalRequestedHealthFacilitiesVaccines.isEmpty()) {
            requestedHealthFacilitiesVaccines = HealthFacilitiesVaccines.builder()
                    .healthFacility(vaccinationSessions.getHealthFacility())
                    .vaccine(optionalVaccine.get())
                    .stock(0L)
                    .build();
            healthFacilitiesVaccinesRepository.save(requestedHealthFacilitiesVaccines);
        } else {
            requestedHealthFacilitiesVaccines = optionalRequestedHealthFacilitiesVaccines.get();
        }

        currentHealthFacilitiesVaccines.setStock(currentHealthFacilitiesVaccines.getStock() + vaccinationSessions.getQuantity());
        if (requestedHealthFacilitiesVaccines.getStock() < request.getQuantity()) {
            throw new BusinessFlowException(HttpStatus.BAD_REQUEST, ResponseMessage.STOCK_NOT_VALID, "Stock is not enough!");
        }
        requestedHealthFacilitiesVaccines.setStock(requestedHealthFacilitiesVaccines.getStock() - request.getQuantity());

        healthFacilitiesVaccinesRepository.save(currentHealthFacilitiesVaccines);
        healthFacilitiesVaccinesRepository.save(requestedHealthFacilitiesVaccines);

        vaccinationSessions.setVaccine(optionalVaccine.get());
        vaccinationSessions.setScheduleStart(request.getScheduleStart());
        vaccinationSessions.setScheduleEnd(request.getScheduleEnd());
        vaccinationSessions.setQuantity(request.getQuantity());
        vaccinationSessionsRepository.save(vaccinationSessions);

        return VaccinationSessionRequest.builder()
                .id(vaccinationSessions.getId())
                .vaccine(VaccineRequest.builder()
                        .id(optionalVaccine.get().getId())
                        .name(optionalVaccine.get().getName())
                        .build())
                .healthFacility(HealthFacilityRequest.builder()
                        .id(vaccinationSessions.getHealthFacility().getId())
                        .name(vaccinationSessions.getHealthFacility().getName())
                        .build())
                .scheduleStart(vaccinationSessions.getScheduleStart())
                .scheduleEnd(vaccinationSessions.getScheduleEnd())
                .quantity(vaccinationSessions.getQuantity())
                .booked(vaccinationSessions.getBooked())
                .build();
    }

    public VaccinationSessionRequest find(Long id) {
        Optional<VaccinationSessions> optionalVaccinationSessions = vaccinationSessionsRepository.findById(id);
        if (optionalVaccinationSessions.isEmpty())
            throw new BusinessFlowException(HttpStatus.BAD_REQUEST, ResponseMessage.BAD_REQUEST, "Vaccination session not found!");
        VaccinationSessions vaccinationSessions = optionalVaccinationSessions.get();
        return VaccinationSessionRequest.builder()
                .id(vaccinationSessions.getId())
                .vaccine(VaccineRequest.builder()
                        .id(vaccinationSessions.getVaccine().getId())
                        .name(vaccinationSessions.getVaccine().getName())
                        .build())
                .healthFacility(HealthFacilityRequest.builder()
                        .id(vaccinationSessions.getHealthFacility().getId())
                        .name(vaccinationSessions.getHealthFacility().getName())
                        .build())
                .scheduleStart(vaccinationSessions.getScheduleStart())
                .scheduleEnd(vaccinationSessions.getScheduleEnd())
                .quantity(vaccinationSessions.getQuantity())
                .booked(vaccinationSessions.getBooked())
                .build();
    }

    public List<VaccinationSessionRequest> findAll(Long cityId) {
        List<VaccinationSessions> vaccinationSessionsList;
        if (cityId == null) {
            vaccinationSessionsList = vaccinationSessionsRepository.findAll();
        } else {
            vaccinationSessionsList = vaccinationSessionsRepository.findAllByHealthFacilityCityId(cityId);
        }
        List<VaccinationSessionRequest> vaccinationSessionRequests = new ArrayList<>();
        for(VaccinationSessions vaccinationSessions : vaccinationSessionsList) {
            vaccinationSessionRequests.add(VaccinationSessionRequest.builder()
                    .id(vaccinationSessions.getId())
                    .vaccine(VaccineRequest.builder()
                            .id(vaccinationSessions.getVaccine().getId())
                            .name(vaccinationSessions.getVaccine().getName())
                            .build())
                    .healthFacility(HealthFacilityRequest.builder()
                            .id(vaccinationSessions.getHealthFacility().getId())
                            .name(vaccinationSessions.getHealthFacility().getName())
                            .build())
                    .scheduleStart(vaccinationSessions.getScheduleStart())
                    .scheduleEnd(vaccinationSessions.getScheduleEnd())
                    .quantity(vaccinationSessions.getQuantity())
                    .booked(vaccinationSessions.getBooked())
                    .build());
        }
        return vaccinationSessionRequests;
    }
}
