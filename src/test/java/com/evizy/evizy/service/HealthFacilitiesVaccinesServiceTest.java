package com.evizy.evizy.service;

import com.evizy.evizy.domain.dao.*;
import com.evizy.evizy.domain.dto.*;
import com.evizy.evizy.errors.BusinessFlowException;
import com.evizy.evizy.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = HealthFacilitiesVaccinesService.class)
class HealthFacilitiesVaccinesServiceTest {
    @MockBean
    private HealthFacilitiesVaccinesRepository healthFacilitiesVaccinesRepository;
    @MockBean
    private HealthFacilityRepository healthFacilityRepository;
    @MockBean
    private VaccinationSessionsRepository vaccinationSessionsRepository;
    @MockBean
    private VaccineRepository vaccineRepository;
    @MockBean
    private VaccineDistributionRepository vaccineDistributionRepository;

    @Autowired
    private HealthFacilitiesVaccinesService healthFacilitiesVaccinesService;

    @Test
    void findAllHealthFacilityVaccinesSuccess_Test() {
        when(healthFacilitiesVaccinesRepository.findAllByHealthFacilityId(1L)).thenReturn(List.of(
                HealthFacilitiesVaccines.builder()
                        .healthFacility(HealthFacility.builder()
                                .id(1L)
                                .build())
                        .vaccine(Vaccine.builder()
                                .id(1L)
                                .build())
                        .stock(10L)
                        .build(),
                HealthFacilitiesVaccines.builder()
                        .healthFacility(HealthFacility.builder()
                                .id(1L)
                                .build())
                        .vaccine(Vaccine.builder()
                                .id(2L)
                                .build())
                        .stock(200L)
                        .build()
        ));

        List<HealthFacilityVaccinesRequest> healthFacilityVaccinesRequests = healthFacilitiesVaccinesService.find(1L);
        assertEquals(1L, healthFacilityVaccinesRequests.get(0).getHealthFacility().getId());
        assertEquals(1L, healthFacilityVaccinesRequests.get(0).getVaccine().getId());
        assertEquals(10L, healthFacilityVaccinesRequests.get(0).getStock());
        assertEquals(1L, healthFacilityVaccinesRequests.get(1).getHealthFacility().getId());
        assertEquals(2L, healthFacilityVaccinesRequests.get(1).getVaccine().getId());
        assertEquals(200L, healthFacilityVaccinesRequests.get(1).getStock());
    }

    @Test
    void distributeHealthFacilityVaccinesHealthFacilityNotFoundFail_Test() {
        when(healthFacilityRepository.findById(any())).thenReturn(Optional.empty());

        try {
            HealthFacilityVaccinesRequest healthFacilityVaccinesRequest = healthFacilitiesVaccinesService.distribute(VaccineDistributionRequest.builder()
                    .healthFacility(HealthFacilityRequest.builder()
                            .id(1L)
                            .build())
                    .vaccine(VaccineRequest.builder()
                            .id(1L)
                            .build())
                    .quantity(10L)
                    .vaccinationSession(VaccinationSessionRequest.builder()
                            .id(1L)
                            .build())
                    .build());
            fail();
        } catch (BusinessFlowException e) {
            assertEquals("Health facility not found!", e.getMessage());
        } catch (Exception e){
            fail();
        }
    }

    @Test
    void distributeHealthFacilityVaccinesVaccineNotFoundFail_Test() {
        when(healthFacilityRepository.findById(any())).thenReturn(Optional.of(
                HealthFacility.builder()
                        .id(1L)
                        .build()
        ));

        when(vaccineRepository.findById(any())).thenReturn(Optional.empty());

        try {
            HealthFacilityVaccinesRequest healthFacilityVaccinesRequest = healthFacilitiesVaccinesService.distribute(VaccineDistributionRequest.builder()
                    .healthFacility(HealthFacilityRequest.builder()
                            .id(1L)
                            .build())
                    .vaccine(VaccineRequest.builder()
                            .id(1L)
                            .build())
                    .quantity(10L)
                    .vaccinationSession(VaccinationSessionRequest.builder()
                            .id(1L)
                            .build())
                    .build());
            fail();
        } catch (BusinessFlowException e) {
            assertEquals("Vaccine not found!", e.getMessage());
        } catch (Exception e){
            fail();
        }
    }

    @Test
    void distributeHealthFacilityVaccinesVaccinationSessionNotFoundFail_Test() {
        when(healthFacilityRepository.findById(any())).thenReturn(Optional.of(
                HealthFacility.builder()
                        .id(1L)
                        .build()
        ));

        when(vaccineRepository.findById(any())).thenReturn(Optional.of(
                Vaccine.builder()
                        .id(1L)
                        .build()
        ));

        when(vaccinationSessionsRepository.findById(any())).thenReturn(Optional.empty());


        try {
            HealthFacilityVaccinesRequest healthFacilityVaccinesRequest = healthFacilitiesVaccinesService.distribute(VaccineDistributionRequest.builder()
                    .healthFacility(HealthFacilityRequest.builder()
                            .id(1L)
                            .build())
                    .vaccine(VaccineRequest.builder()
                            .id(1L)
                            .build())
                    .quantity(10L)
                    .vaccinationSession(VaccinationSessionRequest.builder()
                            .id(1L)
                            .build())
                    .build());
            fail();
        } catch (BusinessFlowException e) {
            assertEquals("Vaccination session not found!", e.getMessage());
        } catch (Exception e){
            fail();
        }
    }

    @Test
    void distributeHealthFacilityVaccinesStockNotEnoughFail_Test() {
        when(healthFacilityRepository.findById(any())).thenReturn(Optional.of(
                HealthFacility.builder()
                        .id(1L)
                        .build()
        ));

        when(vaccineRepository.findById(any())).thenReturn(Optional.of(
                Vaccine.builder()
                        .id(1L)
                        .build()
        ));

        when(vaccinationSessionsRepository.findById(any())).thenReturn(Optional.of(
                VaccinationSessions.builder()
                        .id(1L)
                        .build()
        ));

        when(healthFacilitiesVaccinesRepository.findById(any())).thenReturn(Optional.empty());

        try {
            HealthFacilityVaccinesRequest healthFacilityVaccinesRequest = healthFacilitiesVaccinesService.distribute(VaccineDistributionRequest.builder()
                    .healthFacility(HealthFacilityRequest.builder()
                            .id(1L)
                            .build())
                    .vaccine(VaccineRequest.builder()
                            .id(1L)
                            .build())
                    .quantity(-10L)
                    .vaccinationSession(VaccinationSessionRequest.builder()
                            .id(1L)
                            .build())
                    .build());
            fail();
        } catch (BusinessFlowException e) {
            assertEquals("Stock is not enough!", e.getMessage());
        } catch (Exception e){
            fail();
        }
    }

    @Test
    void distributeHealthFacilitySuccess_Test() {
        when(healthFacilityRepository.findById(any())).thenReturn(Optional.of(
                HealthFacility.builder()
                        .id(1L)
                        .build()
        ));

        when(vaccineRepository.findById(any())).thenReturn(Optional.of(
                Vaccine.builder()
                        .id(1L)
                        .build()
        ));

        when(vaccinationSessionsRepository.findById(any())).thenReturn(Optional.of(
                VaccinationSessions.builder()
                        .id(1L)
                        .build()
        ));

        when(healthFacilitiesVaccinesRepository.findById(any())).thenReturn(Optional.of(
                HealthFacilitiesVaccines.builder()
                        .healthFacility(HealthFacility.builder()
                                .id(1L)
                                .build())
                        .vaccine(Vaccine.builder()
                                .id(1L)
                                .build())
                        .stock(10L)
                        .build()
        ));

        HealthFacilityVaccinesRequest healthFacilityVaccinesRequest = healthFacilitiesVaccinesService.distribute(VaccineDistributionRequest.builder()
                .healthFacility(HealthFacilityRequest.builder()
                        .id(1L)
                        .build())
                .vaccine(VaccineRequest.builder()
                        .id(1L)
                        .build())
                .quantity(10L)
                .vaccinationSession(VaccinationSessionRequest.builder()
                        .id(1L)
                        .build())
                .build());

        assertEquals(20L, healthFacilityVaccinesRequest.getStock());
    }
}