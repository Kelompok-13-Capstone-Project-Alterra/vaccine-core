package com.evizy.evizy.service;

import com.evizy.evizy.domain.dao.Admin;
import com.evizy.evizy.domain.dao.City;
import com.evizy.evizy.domain.dao.HealthFacility;
import com.evizy.evizy.domain.dto.AdminsRequest;
import com.evizy.evizy.domain.dto.CityRequest;
import com.evizy.evizy.domain.dto.HealthFacilityRequest;
import com.evizy.evizy.errors.BusinessFlowException;
import com.evizy.evizy.repository.AdminRepository;
import com.evizy.evizy.repository.CityRepository;
import com.evizy.evizy.repository.HealthFacilityRepository;
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
@SpringBootTest(classes = HealthFacilityService.class)
class HealthFacilityServiceTest {
    @MockBean
    private CityRepository cityRepository;

    @MockBean
    private AdminRepository adminRepository;

    @MockBean
    private HealthFacilityRepository healthFacilityRepository;

    @Autowired
    private HealthFacilityService healthFacilityService;

    @Test
    void createHealthFacilitySuccess_Test() {
        when(adminRepository.findById(any())).thenReturn(Optional.of(Admin.builder()
                .id(1L)
                .name("Admin 1")
                .username("admin1")
                .build()
        ));

        when(cityRepository.findById(any())).thenReturn(Optional.of(
                City.builder()
                        .id(1L)
                        .name("Jakarta")
                        .build()
        ));

        when(healthFacilityRepository.save(any())).thenAnswer(i -> {
            ((HealthFacility) i.getArgument(0)).setId(1L);
            return null;
        });

        HealthFacilityRequest healthFacilityRequest = healthFacilityService.create(HealthFacilityRequest.builder()
                .name("My Health Facility")
                .admin(AdminsRequest.builder()
                        .id(1L)
                        .build())
                .city(CityRequest.builder()
                        .id(1L)
                        .build())
                .build());
        assertEquals(1L, healthFacilityRequest.getId());
        assertEquals("My Health Facility", healthFacilityRequest.getName());
        assertEquals(1L, healthFacilityRequest.getAdmin().getId());
        assertEquals(1L, healthFacilityRequest.getCity().getId());
    }

    @Test
    void createHealthFacilityInvalidAdminFail_Test() {
        when(adminRepository.findById(any())).thenReturn(Optional.empty());

        try {
            HealthFacilityRequest healthFacilityRequest = healthFacilityService.create(HealthFacilityRequest.builder()
                    .name("My Health Facility")
                    .admin(AdminsRequest.builder()
                            .id(1L)
                            .build())
                    .city(CityRequest.builder()
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
    void createHealthFacilityInvalidCityFail_Test() {
        when(adminRepository.findById(any())).thenReturn(Optional.of(Admin.builder()
                .id(1L)
                .name("Admin 1")
                .username("admin1")
                .build()
        ));

        when(cityRepository.findById(any())).thenReturn(Optional.empty());

        try {
            HealthFacilityRequest healthFacilityRequest = healthFacilityService.create(HealthFacilityRequest.builder()
                    .name("My Health Facility")
                    .admin(AdminsRequest.builder()
                            .id(1L)
                            .build())
                    .city(CityRequest.builder()
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
    void deleteHealthFacilityFail_Test() {
        when(healthFacilityRepository.findById(any())).thenReturn(Optional.empty());
        doNothing().when(healthFacilityRepository).delete(any());

        try {
            healthFacilityService.delete(1L);
            fail();
        } catch (BusinessFlowException e) {
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void deleteHealthFacilitySuccess_Test() {
        when(healthFacilityRepository.findById(any())).thenReturn(Optional.of(HealthFacility.builder()
                .id(1L)
                .name("My Health Facility")
                .admin(Admin.builder()
                        .id(1L)
                        .build())
                .city(City.builder()
                        .id(1L)
                        .build())
                .build()));
        doNothing().when(healthFacilityRepository).delete(any());
        healthFacilityService.delete(1L);
    }

    @Test
    void findAllHealthFacilitySuccess_Test() {
        when(healthFacilityRepository.findAll()).thenReturn(List.of(
                HealthFacility.builder()
                        .id(1L)
                        .name("My Health Facility 1")
                        .admin(Admin.builder()
                                .id(1L)
                                .build())
                        .city(City.builder()
                                .id(1L)
                                .build())
                        .build(),
                HealthFacility.builder()
                        .id(2L)
                        .name("My Health Facility 2")
                        .admin(Admin.builder()
                                .id(2L)
                                .build())
                        .city(City.builder()
                                .id(2L)
                                .build())
                        .build()
        ));

        List<HealthFacilityRequest> healthFacilities = healthFacilityService.findAll(null);
        assertEquals(1L, healthFacilities.get(0).getId());
        assertEquals("My Health Facility 1", healthFacilities.get(0).getName());
        assertEquals(1L, healthFacilities.get(0).getCity().getId());
        assertEquals(1L, healthFacilities.get(0).getAdmin().getId());
        assertEquals(2L, healthFacilities.get(1).getId());
        assertEquals("My Health Facility 2", healthFacilities.get(1).getName());
        assertEquals(2L, healthFacilities.get(1).getCity().getId());
        assertEquals(2L, healthFacilities.get(1).getAdmin().getId());
    }

    @Test
    void findAllHealthFacilityByCityIdSuccess_Test() {
        when(healthFacilityRepository.findAllByCityId(any())).thenReturn(List.of(
                HealthFacility.builder()
                        .id(1L)
                        .name("My Health Facility 1")
                        .admin(Admin.builder()
                                .id(1L)
                                .build())
                        .city(City.builder()
                                .id(1L)
                                .build())
                        .build(),
                HealthFacility.builder()
                        .id(3L)
                        .name("My Health Facility 3")
                        .admin(Admin.builder()
                                .id(2L)
                                .build())
                        .city(City.builder()
                                .id(1L)
                                .build())
                        .build()
        ));

        List<HealthFacilityRequest> healthFacilities = healthFacilityService.findAll(1L);
        assertEquals(1L, healthFacilities.get(0).getId());
        assertEquals("My Health Facility 1", healthFacilities.get(0).getName());
        assertEquals(1L, healthFacilities.get(0).getCity().getId());
        assertEquals(1L, healthFacilities.get(0).getAdmin().getId());
        assertEquals(3L, healthFacilities.get(1).getId());
        assertEquals("My Health Facility 3", healthFacilities.get(1).getName());
        assertEquals(1L, healthFacilities.get(1).getCity().getId());
        assertEquals(2L, healthFacilities.get(1).getAdmin().getId());
    }

    @Test
    void findHealthFacilityByIdSuccess_Test() {
        when(healthFacilityRepository.findById(any())).thenReturn(Optional.of(
                HealthFacility.builder()
                        .id(1L)
                        .name("My Health Facility 1")
                        .admin(Admin.builder()
                                .id(1L)
                                .build())
                        .city(City.builder()
                                .id(1L)
                                .build())
                        .build()
        ));

        HealthFacilityRequest healthFacilityRequest = healthFacilityService.find(1L);
        assertEquals(1L, healthFacilityRequest.getId());
        assertEquals("My Health Facility 1", healthFacilityRequest.getName());
        assertEquals(1L, healthFacilityRequest.getCity().getId());
        assertEquals(1L, healthFacilityRequest.getAdmin().getId());
    }

    @Test
    void findHealthFacilityByIdFail_Test() {
        when(cityRepository.findById(any())).thenReturn(Optional.empty());

        try {
            HealthFacilityRequest healthFacility = healthFacilityService.find(1L);
            fail();
        } catch (BusinessFlowException e) {
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void updateHealthFacilityNotFoundFail_Test() {
        when(healthFacilityRepository.findById(any())).thenReturn(Optional.empty());

        try {
             HealthFacilityRequest healthFacility = healthFacilityService.update(1L, HealthFacilityRequest.builder()
                     .name("New Health Facility")
                     .admin(AdminsRequest.builder()
                             .id(1L)
                             .build())
                     .city(CityRequest.builder()
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
    void updateHealthFacilityCityInvalidFail_Test() {
        when(healthFacilityRepository.findById(any())).thenReturn(Optional.of(
                HealthFacility.builder()
                        .name("My Health Facility")
                        .admin(Admin.builder()
                                .id(1L)
                                .build())
                        .city(City.builder()
                                .id(1L)
                                .build())
                        .build()
        ));
        when(cityRepository.findById(any())).thenReturn(Optional.empty());

        try {
            HealthFacilityRequest healthFacility = healthFacilityService.update(1L, HealthFacilityRequest.builder()
                    .name("New Health Facility")
                    .admin(AdminsRequest.builder()
                            .id(1L)
                            .build())
                    .city(CityRequest.builder()
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
    void updateHealthFacilityAdminInvalidFail_Test() {
        when(healthFacilityRepository.findById(any())).thenReturn(Optional.of(
                HealthFacility.builder()
                        .name("My Health Facility")
                        .admin(Admin.builder()
                                .id(1L)
                                .build())
                        .city(City.builder()
                                .id(1L)
                                .build())
                        .build()
        ));
        when(cityRepository.findById(any())).thenReturn(Optional.of(
                City.builder()
                        .id(1L)
                        .name("Jakarta")
                        .build()
        ));
        when(adminRepository.findById(any())).thenReturn(Optional.empty());

        try {
            HealthFacilityRequest healthFacility = healthFacilityService.update(1L, HealthFacilityRequest.builder()
                    .name("New Health Facility")
                    .admin(AdminsRequest.builder()
                            .id(1L)
                            .build())
                    .city(CityRequest.builder()
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
    void updateHealthFacilitySuccess_Test() {
        when(healthFacilityRepository.findById(any())).thenReturn(Optional.of(
                HealthFacility.builder()
                        .id(1L)
                        .name("My Health Facility")
                        .admin(Admin.builder()
                                .id(1L)
                                .build())
                        .city(City.builder()
                                .id(1L)
                                .build())
                        .build()
        ));
        when(cityRepository.findById(any())).thenReturn(Optional.of(
                City.builder()
                        .id(1L)
                        .name("Jakarta")
                        .build()
        ));
        when(adminRepository.findById(any())).thenReturn(Optional.of(
                Admin.builder()
                        .id(1L)
                        .build()
        ));

        HealthFacilityRequest healthFacility = healthFacilityService.update(1L, HealthFacilityRequest.builder()
                .name("New Health Facility")
                .admin(AdminsRequest.builder()
                        .id(1L)
                        .build())
                .city(CityRequest.builder()
                        .id(1L)
                        .build())
                .build());
        assertEquals("New Health Facility", healthFacility.getName());
        assertEquals(1L, healthFacility.getId());
        assertEquals(1L, healthFacility.getAdmin().getId());
        assertEquals(1L, healthFacility.getCity().getId());
    }
}