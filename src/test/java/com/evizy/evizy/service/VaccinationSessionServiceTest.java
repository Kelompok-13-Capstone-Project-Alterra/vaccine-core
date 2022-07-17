package com.evizy.evizy.service;

import com.evizy.evizy.domain.dao.*;
import com.evizy.evizy.domain.dto.CityRequest;
import com.evizy.evizy.domain.dto.HealthFacilityRequest;
import com.evizy.evizy.domain.dto.VaccinationSessionRequest;
import com.evizy.evizy.domain.dto.VaccineRequest;
import com.evizy.evizy.errors.BusinessFlowException;
import com.evizy.evizy.repository.HealthFacilitiesVaccinesRepository;
import com.evizy.evizy.repository.HealthFacilityRepository;
import com.evizy.evizy.repository.VaccinationSessionsRepository;
import com.evizy.evizy.repository.VaccineRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = VaccinationSessionService.class)
class VaccinationSessionServiceTest {
    @MockBean
    private VaccinationSessionsRepository vaccinationSessionsRepository;
    @MockBean
    private HealthFacilityRepository healthFacilityRepository;
    @MockBean
    private HealthFacilitiesVaccinesRepository healthFacilitiesVaccinesRepository;
    @MockBean
    private VaccineRepository vaccineRepository;


    @Autowired
    private VaccinationSessionService vaccinationSessionService;

    @Test
    void createVaccinationSessionSuccess_Test() {
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

        when(vaccinationSessionsRepository.save(any())).thenAnswer(i -> {
            ((VaccinationSessions) i.getArgument(0)).setId(1L);
            return null;
        });

        VaccinationSessionRequest vaccinationSessionRequest = vaccinationSessionService.create(VaccinationSessionRequest.builder()
                .scheduleDate(LocalDate.of(2022, 12, 25))
                .scheduleTimeStart("20:00")
                .scheduleTimeEnd("23:00")
                .quantity(10L)
                .vaccine(VaccineRequest.builder()
                        .id(1L)
                        .build())
                .healthFacility(HealthFacilityRequest.builder()
                        .id(1L)
                        .build())
                .build());
        assertEquals(1L, vaccinationSessionRequest.getId());
        assertEquals("20:00", vaccinationSessionRequest.getScheduleTimeStart());
        assertEquals("23:00", vaccinationSessionRequest.getScheduleTimeEnd());
        assertEquals(10L, vaccinationSessionRequest.getQuantity());
    }

    @Test
    void createVaccinationSessionHealthFacilityNotFoundFail_Test() {
        when(healthFacilityRepository.findById(any())).thenReturn(Optional.empty());

        when(vaccineRepository.findById(any())).thenReturn(Optional.of(
                Vaccine.builder()
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

        when(vaccinationSessionsRepository.save(any())).thenAnswer(i -> {
            ((VaccinationSessions) i.getArgument(0)).setId(1L);
            return null;
        });

        try {
            VaccinationSessionRequest vaccinationSessionRequest = vaccinationSessionService.create(VaccinationSessionRequest.builder()
                    .scheduleDate(LocalDate.of(2022, 12, 25))
                    .scheduleTimeStart("20:00")
                    .scheduleTimeEnd("23:00")
                    .quantity(10L)
                    .vaccine(VaccineRequest.builder()
                            .id(1L)
                            .build())
                    .healthFacility(HealthFacilityRequest.builder()
                            .id(1L)
                            .build())
                    .build());
            fail();
        } catch (BusinessFlowException e) {
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void createVaccinationSessionVaccineNotFoundFail_Test() {
        when(healthFacilityRepository.findById(any())).thenReturn(Optional.of(
                HealthFacility.builder()
                        .id(1L)
                        .build()
        ));

        when(vaccineRepository.findById(any())).thenReturn(Optional.empty());

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

        when(vaccinationSessionsRepository.save(any())).thenAnswer(i -> {
            ((VaccinationSessions) i.getArgument(0)).setId(1L);
            return null;
        });

        try {
            VaccinationSessionRequest vaccinationSessionRequest = vaccinationSessionService.create(VaccinationSessionRequest.builder()
                    .scheduleDate(LocalDate.of(2022, 12, 25))
                    .scheduleTimeStart("20:00")
                    .scheduleTimeEnd("23:00")
                    .quantity(10L)
                    .vaccine(VaccineRequest.builder()
                            .id(1L)
                            .build())
                    .healthFacility(HealthFacilityRequest.builder()
                            .id(1L)
                            .build())
                    .build());
            fail();
        } catch (BusinessFlowException e) {
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void createVaccinationSessionStockNotEnoughFail_Test() {
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

        when(healthFacilitiesVaccinesRepository.findById(any())).thenReturn(Optional.empty());

        when(vaccinationSessionsRepository.save(any())).thenAnswer(i -> {
            ((VaccinationSessions) i.getArgument(0)).setId(1L);
            return null;
        });

        try {
            VaccinationSessionRequest vaccinationSessionRequest = vaccinationSessionService.create(VaccinationSessionRequest.builder()
                    .scheduleDate(LocalDate.of(2022, 12, 25))
                    .scheduleTimeStart("20:00")
                    .scheduleTimeEnd("23:00")
                    .quantity(10L)
                    .vaccine(VaccineRequest.builder()
                            .id(1L)
                            .build())
                    .healthFacility(HealthFacilityRequest.builder()
                            .id(1L)
                            .build())
                    .build());
            fail();
        } catch (BusinessFlowException e) {
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void deleteVaccinationSessionNotFoundFail_Test() {
        when(vaccinationSessionsRepository.findById(any())).thenReturn(Optional.empty());
        doNothing().when(vaccinationSessionsRepository).delete(any());

        try {
            vaccinationSessionService.delete(1L);
            fail();
        } catch (BusinessFlowException e) {
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void deleteVaccinationSessionAlreadyBookedFail_Test() {
        when(vaccinationSessionsRepository.findById(any())).thenReturn(Optional.of(VaccinationSessions.builder()
                .id(1L)
                .quantity(1L)
                .booked(1L)
                .build()));
        doNothing().when(vaccinationSessionsRepository).delete(any());

        try {
            vaccinationSessionService.delete(1L);
            fail();
        } catch (BusinessFlowException e) {
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void deleteVaccinationSuccess_Test() {
        when(vaccinationSessionsRepository.findById(any())).thenReturn(Optional.of(VaccinationSessions.builder()
                .id(1L)
                .healthFacility(HealthFacility.builder()
                        .id(1L)
                        .build())
                .vaccine(Vaccine.builder()
                        .id(1L)
                        .build())
                .booked(0L)
                .build()));
        doNothing().when(vaccinationSessionsRepository).delete(any());
        vaccinationSessionService.delete(1L);
    }

    @Test
    void deleteVaccinationExistingVaccineSuccess_Test() {
        when(vaccinationSessionsRepository.findById(any())).thenReturn(Optional.of(VaccinationSessions.builder()
                .id(1L)
                .healthFacility(HealthFacility.builder()
                        .id(1L)
                        .build())
                .vaccine(Vaccine.builder()
                        .id(1L)
                        .build())
                .quantity(10L)
                .booked(0L)
                .build()));
        when(healthFacilitiesVaccinesRepository.findById(any())).thenReturn(Optional.of(HealthFacilitiesVaccines.builder()
                .healthFacility(HealthFacility.builder()
                        .id(1L)
                        .build())
                .vaccine(Vaccine.builder()
                        .id(1L)
                        .build())
                .stock(0L)
                .build()));
        doNothing().when(vaccinationSessionsRepository).delete(any());
        vaccinationSessionService.delete(1L);
    }

    @Test
    void findAllVaccinationSessionSuccess_Test() {
        when(vaccinationSessionsRepository.findAll()).thenReturn(List.of(
                VaccinationSessions.builder()
                        .id(1L)
                        .healthFacility(HealthFacility
                                .builder()
                                .id(1L)
                                .build())
                        .vaccine(Vaccine.builder()
                                .id(1L)
                                .build())
                        .quantity(10L)
                        .booked(0L)
                        .scheduleDate(LocalDate.of(2022, 12, 25))
                        .scheduleTimeStart("20:00")
                        .scheduleTimeEnd("23:00")
                        .build(),
                VaccinationSessions.builder()
                        .id(2L)
                        .healthFacility(HealthFacility
                                .builder()
                                .id(2L)
                                .build())
                        .vaccine(Vaccine.builder()
                                .id(1L)
                                .build())
                        .quantity(200L)
                        .booked(50L)
                        .scheduleDate(LocalDate.of(2022, 12, 25))
                        .scheduleTimeStart("20:00")
                        .scheduleTimeEnd("23:00")
                        .build()
        ));

        List<VaccinationSessionRequest> vaccinationSessionRequests = vaccinationSessionService.findAll(null);
        assertEquals(1L, vaccinationSessionRequests.get(0).getId());
        assertEquals(10L, vaccinationSessionRequests.get(0).getQuantity());
        assertEquals(0L, vaccinationSessionRequests.get(0).getBooked());
        assertEquals(2L, vaccinationSessionRequests.get(1).getId());
        assertEquals(200L, vaccinationSessionRequests.get(1).getQuantity());
        assertEquals(50L, vaccinationSessionRequests.get(1).getBooked());
    }

    @Test
    void findAllVaccinationSessionByHealthFacilitySuccess_Test() {
        when(vaccinationSessionsRepository.findAllByHealthFacilityId(any())).thenReturn(List.of(
                VaccinationSessions.builder()
                        .id(1L)
                        .healthFacility(HealthFacility
                                .builder()
                                .id(1L)
                                .build())
                        .vaccine(Vaccine.builder()
                                .id(1L)
                                .build())
                        .quantity(10L)
                        .booked(0L)
                        .scheduleDate(LocalDate.of(2022, 12, 25))
                        .scheduleTimeStart("20:00")
                        .scheduleTimeEnd("23:00")
                        .build(),
                VaccinationSessions.builder()
                        .id(2L)
                        .healthFacility(HealthFacility
                                .builder()
                                .id(1L)
                                .build())
                        .vaccine(Vaccine.builder()
                                .id(2L)
                                .build())
                        .quantity(200L)
                        .booked(50L)
                        .scheduleDate(LocalDate.of(2022, 12, 25))
                        .scheduleTimeStart("20:00")
                        .scheduleTimeEnd("23:00")
                        .build()
        ));

        List<VaccinationSessionRequest> vaccinationSessionRequests = vaccinationSessionService.findAll(1L);
        assertEquals(1L, vaccinationSessionRequests.get(0).getId());
        assertEquals(10L, vaccinationSessionRequests.get(0).getQuantity());
        assertEquals(0L, vaccinationSessionRequests.get(0).getBooked());
        assertEquals(2L, vaccinationSessionRequests.get(1).getId());
        assertEquals(200L, vaccinationSessionRequests.get(1).getQuantity());
        assertEquals(50L, vaccinationSessionRequests.get(1).getBooked());
    }

    @Test
    void findVaccinationSessionByIdSuccess_Test() {
        when(vaccinationSessionsRepository.findById(any())).thenReturn(Optional.of(
                VaccinationSessions.builder()
                        .id(1L)
                        .healthFacility(HealthFacility
                                .builder()
                                .id(1L)
                                .build())
                        .vaccine(Vaccine.builder()
                                .id(1L)
                                .build())
                        .quantity(10L)
                        .booked(0L)
                        .scheduleDate(LocalDate.of(2022, 12, 25))
                        .scheduleTimeStart("20:00")
                        .scheduleTimeEnd("23:00")
                        .build()
        ));

        VaccinationSessionRequest vaccinationSessionRequest = vaccinationSessionService.find(1L);
        assertEquals(10L, vaccinationSessionRequest.getQuantity());
        assertEquals(0L, vaccinationSessionRequest.getBooked());
        assertEquals(1L, vaccinationSessionRequest.getId());
    }

    @Test
    void findVaccinationSessionByIdFail_Test() {
        when(vaccinationSessionsRepository.findById(any())).thenReturn(Optional.empty());

        try {
            VaccinationSessionRequest vaccinationSessionRequest = vaccinationSessionService.find(1L);
            fail();
        } catch (BusinessFlowException e) {
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void updateVaccinationSessionAlreadyBookedFail_Test() {
        when(vaccinationSessionsRepository.findById(any())).thenReturn(Optional.of(
                VaccinationSessions.builder()
                        .id(1L)
                        .healthFacility(HealthFacility
                                .builder()
                                .id(1L)
                                .build())
                        .vaccine(Vaccine.builder()
                                .id(1L)
                                .build())
                        .quantity(10L)
                        .booked(5L)
                        .scheduleDate(LocalDate.of(2022, 12, 25))
                        .scheduleTimeStart("20:00")
                        .scheduleTimeEnd("23:00")
                        .build()
        ));

        try {
            VaccinationSessionRequest vaccinationSession = vaccinationSessionService.update(1L, VaccinationSessionRequest.builder()
                    .scheduleDate(LocalDate.of(2022, 12, 25))
                    .scheduleTimeStart("20:00")
                    .scheduleTimeEnd("23:00")
                    .quantity(10L)
                    .vaccine(VaccineRequest.builder()
                            .id(1L)
                            .build())
                    .healthFacility(HealthFacilityRequest.builder()
                            .id(1L)
                            .build())
                    .build());
            fail();
        } catch (BusinessFlowException e) {

        } catch (Exception e){
            fail();
        }
    }

    @Test
    void updateVaccinationSessionInvalidVaccineFail_Test() {
        when(vaccinationSessionsRepository.findById(any())).thenReturn(Optional.of(
                VaccinationSessions.builder()
                        .id(1L)
                        .healthFacility(HealthFacility
                                .builder()
                                .id(1L)
                                .build())
                        .vaccine(Vaccine.builder()
                                .id(1L)
                                .build())
                        .quantity(10L)
                        .booked(0L)
                        .scheduleDate(LocalDate.of(2022, 12, 25))
                        .scheduleTimeStart("20:00")
                        .scheduleTimeEnd("23:00")
                        .build()
        ));

        when(vaccineRepository.findById(any())).thenReturn(Optional.empty());

        try {
            VaccinationSessionRequest vaccinationSession = vaccinationSessionService.update(1L, VaccinationSessionRequest.builder()
                    .scheduleDate(LocalDate.of(2022, 12, 25))
                    .scheduleTimeStart("20:00")
                    .scheduleTimeEnd("23:00")
                    .quantity(10L)
                    .vaccine(VaccineRequest.builder()
                            .id(1L)
                            .build())
                    .healthFacility(HealthFacilityRequest.builder()
                            .id(1L)
                            .build())
                    .build());
            fail();
        } catch (BusinessFlowException e) {

        } catch (Exception e){
            fail();
        }
    }

    @Test
    void updateVaccinationSessionStockNotEnoughFail_Test() {
        when(vaccinationSessionsRepository.findById(any())).thenReturn(Optional.of(
                VaccinationSessions.builder()
                        .id(1L)
                        .healthFacility(HealthFacility
                                .builder()
                                .id(1L)
                                .build())
                        .vaccine(Vaccine.builder()
                                .id(1L)
                                .build())
                        .quantity(10L)
                        .booked(0L)
                        .scheduleDate(LocalDate.of(2022, 12, 25))
                        .scheduleTimeStart("20:00")
                        .scheduleTimeEnd("23:00")
                        .build()
        ));

        when(vaccineRepository.findById(any())).thenReturn(Optional.of(
                Vaccine.builder()
                        .id(1L)
                        .name("Sinovac")
                        .build()
        ));

        when(healthFacilitiesVaccinesRepository.findById(new HealthFacilitiesVaccines.HealthFacilitiesVaccinesId(1L, 1L))).thenReturn(Optional.of(
                HealthFacilitiesVaccines.builder()
                        .healthFacility(HealthFacility
                                .builder()
                                .id(1L)
                                .build())
                        .vaccine(Vaccine.builder()
                                .id(1L)
                                .build())
                        .stock(10L)
                        .build()
        ));
        when(healthFacilitiesVaccinesRepository.findById(new HealthFacilitiesVaccines.HealthFacilitiesVaccinesId(1L, 2L))).thenReturn(Optional.empty());

        try {
            VaccinationSessionRequest vaccinationSession = vaccinationSessionService.update(1L, VaccinationSessionRequest.builder()
                    .scheduleDate(LocalDate.of(2022, 12, 25))
                    .scheduleTimeStart("20:00")
                    .scheduleTimeEnd("23:00")
                    .quantity(10L)
                    .vaccine(VaccineRequest.builder()
                            .id(2L)
                            .build())
                    .healthFacility(HealthFacilityRequest.builder()
                            .id(1L)
                            .build())
                    .build());
            fail();
        } catch (BusinessFlowException e) {

        } catch (Exception e){
            fail();
        }
    }

    @Test
    void updateVaccinationSessionNotFoundFail_Test() {
        when(vaccinationSessionsRepository.findById(any())).thenReturn(Optional.empty());

        try {
            VaccinationSessionRequest vaccinationSession = vaccinationSessionService.update(1L, VaccinationSessionRequest.builder()
                    .scheduleDate(LocalDate.of(2022, 12, 25))
                    .scheduleTimeStart("20:00")
                    .scheduleTimeEnd("23:00")
                    .quantity(10L)
                    .vaccine(VaccineRequest.builder()
                            .id(1L)
                            .build())
                    .healthFacility(HealthFacilityRequest.builder()
                            .id(1L)
                            .build())
                    .build());
            fail();
        } catch (BusinessFlowException e) {

        } catch (Exception e){
            fail();
        }
    }

    @Test
    void updateCitySuccess_Test() {
        when(vaccinationSessionsRepository.findById(any())).thenReturn(Optional.of(
                VaccinationSessions.builder()
                        .id(1L)
                        .healthFacility(HealthFacility
                                .builder()
                                .id(1L)
                                .build())
                        .vaccine(Vaccine.builder()
                                .id(1L)
                                .build())
                        .quantity(20L)
                        .booked(0L)
                        .scheduleDate(LocalDate.of(2022, 12, 25))
                        .scheduleTimeStart("20:00")
                        .scheduleTimeEnd("23:00")
                        .build()
        ));

        when(vaccineRepository.findById(any())).thenReturn(Optional.of(
                Vaccine.builder()
                        .id(1L)
                        .name("Sinovac")
                        .build()
        ));

        when(healthFacilitiesVaccinesRepository.findById(any())).thenReturn(Optional.of(
                HealthFacilitiesVaccines.builder()
                        .vaccine(Vaccine.builder()
                                .id(1L)
                                .build())
                        .healthFacility(HealthFacility.builder()
                                .id(1L)
                                .build())
                        .stock(10L)
                        .build()
        ));

        VaccinationSessionRequest vaccinationSession = vaccinationSessionService.update(1L, VaccinationSessionRequest.builder()
                .scheduleDate(LocalDate.of(2022, 12, 25))
                .scheduleTimeStart("20:00")
                .scheduleTimeEnd("23:00")
                .quantity(10L)
                .vaccine(VaccineRequest.builder()
                        .id(1L)
                        .build())
                .healthFacility(HealthFacilityRequest.builder()
                        .id(1L)
                        .build())
                .build());
        assertEquals(10L, vaccinationSession.getQuantity());
        assertEquals(1L, vaccinationSession.getId());
    }
}