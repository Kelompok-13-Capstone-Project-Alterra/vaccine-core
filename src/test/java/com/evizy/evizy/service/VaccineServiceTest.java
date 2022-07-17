package com.evizy.evizy.service;

import com.evizy.evizy.domain.dao.City;
import com.evizy.evizy.domain.dao.Vaccine;
import com.evizy.evizy.domain.dto.CityRequest;
import com.evizy.evizy.domain.dto.VaccineRequest;
import com.evizy.evizy.errors.BusinessFlowException;
import com.evizy.evizy.repository.CityRepository;
import com.evizy.evizy.repository.VaccineRepository;
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
@SpringBootTest(classes = VaccineService.class)
class VaccineServiceTest {
    @MockBean
    private VaccineRepository vaccineRepository;

    @Autowired
    private VaccineService vaccineService;

    @Test
    void createVaccineSuccess_Test() {
        when(vaccineRepository.countAllVaccinesByLowerName(any())).thenReturn(0L);
        when(vaccineRepository.save(any())).thenAnswer(i -> {
            ((Vaccine) i.getArgument(0)).setId(1L);
            return null;
        });

        VaccineRequest vaccine = vaccineService.create(VaccineRequest.builder()
                .name("Sinovac")
                .build());
        assertEquals(1L, vaccine.getId());
        assertEquals("Sinovac", vaccine.getName());
    }

    @Test
    void createVaccineFail_Test() {
        when(vaccineRepository.countAllVaccinesByLowerName(any())).thenReturn(1L);

        try {
            VaccineRequest vaccine = vaccineService.create(VaccineRequest.builder()
                    .name("Sinovac")
                    .build());
            fail();
        } catch (BusinessFlowException e) {
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void deleteVaccineFail_Test() {
        when(vaccineRepository.findById(any())).thenReturn(Optional.empty());
        doNothing().when(vaccineRepository).delete(any());

        try {
            vaccineService.delete(1L);
            fail();
        } catch (BusinessFlowException e) {
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void deleteVaccineSuccess_Test() {
        when(vaccineRepository.findById(any())).thenReturn(Optional.of(Vaccine.builder()
                .id(1L)
                .name("Moderna")
                .build()));
        doNothing().when(vaccineRepository).delete(any());
        vaccineService.delete(1L);
    }

    @Test
    void findAllVaccinesSuccess_Test() {
        when(vaccineRepository.findAll()).thenReturn(List.of(
                Vaccine.builder()
                        .id(1L)
                        .name("Sinovac")
                        .build(),
                Vaccine.builder()
                        .id(2L)
                        .name("Moderna")
                        .build()
        ));

        List<VaccineRequest> vaccines = vaccineService.find();
        assertEquals(1L, vaccines.get(0).getId());
        assertEquals("Sinovac", vaccines.get(0).getName());
        assertEquals(2L, vaccines.get(1).getId());
        assertEquals("Moderna", vaccines.get(1).getName());
    }

    @Test
    void findVaccineByIdSuccess_Test() {
        when(vaccineRepository.findById(any())).thenReturn(Optional.of(
                Vaccine.builder()
                        .id(1L)
                        .name("Sinovac")
                        .build()
        ));

        VaccineRequest vaccine = vaccineService.find(1L);
        assertEquals("Sinovac", vaccine.getName());
        assertEquals(1L, vaccine.getId());
    }

    @Test
    void findVaccineByIdFail_Test() {
        when(vaccineRepository.findById(any())).thenReturn(Optional.empty());

        try {
            VaccineRequest vaccine = vaccineService.find(1L);
            fail();
        } catch (BusinessFlowException e) {
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void updateVaccineFail_Test() {
        when(vaccineRepository.findById(any())).thenReturn(Optional.empty());

        try {
            VaccineRequest vaccine = vaccineService.update(1L, VaccineRequest.builder()
                    .name("Sinovac")
                    .build());
            fail();
        } catch (BusinessFlowException e) {
        } catch (Exception e){
            fail();
        }
    }

    @Test
    void updateVaccineSuccess_Test() {
        when(vaccineRepository.findById(any())).thenReturn(Optional.of(Vaccine.builder()
                .id(1L)
                .name("Sinovac")
                .build()));

        VaccineRequest vaccine = vaccineService.update(1L, VaccineRequest.builder()
                .name("Moderna")
                .build());
        assertEquals("Moderna", vaccine.getName());
        assertEquals(1L, vaccine.getId());
    }
}