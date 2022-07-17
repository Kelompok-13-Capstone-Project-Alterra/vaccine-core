package com.evizy.evizy.service;

import com.evizy.evizy.domain.dao.Admin;
import com.evizy.evizy.domain.dao.City;
import com.evizy.evizy.domain.dto.AdminsRequest;
import com.evizy.evizy.domain.dto.CityRequest;
import com.evizy.evizy.errors.BusinessFlowException;
import com.evizy.evizy.repository.CityRepository;
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
@SpringBootTest(classes = CityService.class)
class CityServiceTest {
    @MockBean
    private CityRepository cityRepository;

    @Autowired
    private CityService cityService;

    @Test
    void createCitySuccess_Test() {
        when(cityRepository.countAllCitiesByLowerName(any())).thenReturn(0L);
        when(cityRepository.save(any())).thenAnswer(i -> {
            ((City) i.getArgument(0)).setId(1L);
            return null;
        });

        CityRequest cityRequest = cityService.create(CityRequest.builder()
                .name("Jakarta")
                .build());
        assertEquals(1L, cityRequest.getId());
        assertEquals("Jakarta", cityRequest.getName());
    }

    @Test
    void createCityFail_Test() {
        when(cityRepository.countAllCitiesByLowerName(any())).thenReturn(1L);

        try {
            CityRequest cityRequest = cityService.create(CityRequest.builder()
                    .name("Jakarta")
                    .build());
            fail();
        } catch (BusinessFlowException e) {
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void deleteCityFail_Test() {
        when(cityRepository.findById(any())).thenReturn(Optional.empty());
        doNothing().when(cityRepository).delete(any());

        try {
            cityService.delete(1L);
            fail();
        } catch (BusinessFlowException e) {
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void deleteCitySuccess_Test() {
        when(cityRepository.findById(any())).thenReturn(Optional.of(City.builder()
                .id(1L)
                .name("Jakarta")
                .build()));
        doNothing().when(cityRepository).delete(any());
        cityService.delete(1L);
    }

    @Test
    void findAllCitiesSuccess_Test() {
        when(cityRepository.findAll()).thenReturn(List.of(
                City.builder()
                        .id(1L)
                        .name("Jakarta")
                        .build(),
                City.builder()
                        .id(2L)
                        .name("Medan")
                        .build()
        ));

        List<CityRequest> cities = cityService.find();
        assertEquals(1L, cities.get(0).getId());
        assertEquals("Jakarta", cities.get(0).getName());
        assertEquals(2L, cities.get(1).getId());
        assertEquals("Medan", cities.get(1).getName());
    }

    @Test
    void findCityByIdSuccess_Test() {
        when(cityRepository.findById(any())).thenReturn(Optional.of(
                City.builder()
                        .id(1L)
                        .name("Jakarta")
                        .build()
        ));

        CityRequest city = cityService.find(1L);
        assertEquals("Jakarta", city.getName());
        assertEquals(1L, city.getId());
    }

    @Test
    void findCityByIdFail_Test() {
        when(cityRepository.findById(any())).thenReturn(Optional.empty());

        try {
            CityRequest city = cityService.find(1L);
            fail();
        } catch (BusinessFlowException e) {
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void updateCityFail_Test() {
        when(cityRepository.findById(any())).thenReturn(Optional.empty());

        try {
            CityRequest city = cityService.update(1L, CityRequest.builder()
                    .name("Medan")
                    .build());
            fail();
        } catch (BusinessFlowException e) {

        } catch (Exception e){
            fail();
        }
    }

    @Test
    void updateCitySuccess_Test() {
        when(cityRepository.findById(any())).thenReturn(Optional.of(City.builder()
                .id(1L)
                .name("Jakarta")
                .build()));

        CityRequest city = cityService.update(1L, CityRequest.builder()
                .name("Medan")
                .build());
        assertEquals("Medan", city.getName());
        assertEquals(1L, city.getId());
    }
}