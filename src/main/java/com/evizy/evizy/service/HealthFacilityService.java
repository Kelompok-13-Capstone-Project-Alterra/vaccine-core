package com.evizy.evizy.service;

import com.evizy.evizy.constant.ResponseMessage;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class HealthFacilityService {
    private final HealthFacilityRepository healthFacilityRepository;
    private final AdminRepository adminRepository;
    private final CityRepository cityRepository;

    public HealthFacilityRequest create(HealthFacilityRequest request) throws BusinessFlowException {
        Optional<Admin> optionalAdmin = adminRepository.findById(request.getAdmin().getId());
        if (optionalAdmin.isEmpty()) {
            throw new BusinessFlowException(HttpStatus.BAD_REQUEST, ResponseMessage.BAD_REQUEST, "Admin not found!");
        }

        Optional<City> optionalCity = cityRepository.findById(request.getCity().getId());
        if (optionalCity.isEmpty()) {
            throw new BusinessFlowException(HttpStatus.BAD_REQUEST, ResponseMessage.BAD_REQUEST, "City not found!");
        }

        City city = optionalCity.get();
        Admin admin = optionalAdmin.get();

        HealthFacility healthFacility = HealthFacility.builder()
                .name(request.getName())
                .admin(admin)
                .city(city)
                .build();
        healthFacilityRepository.save(healthFacility);

        return HealthFacilityRequest.builder()
                .id(healthFacility.getId())
                .name(healthFacility.getName())
                .admin(AdminsRequest
                        .builder()
                        .id(healthFacility.getAdmin().getId())
                        .name(healthFacility.getAdmin().getName())
                        .build())
                .city(CityRequest
                        .builder()
                        .id(healthFacility.getCity().getId())
                        .name(healthFacility.getCity().getName())
                        .build())
                .build();
    }

    public void delete(Long id) throws BusinessFlowException {
        Optional<HealthFacility> optionalHealthFacility = healthFacilityRepository.findById(id);
        if (optionalHealthFacility.isEmpty()) {
            throw new BusinessFlowException(HttpStatus.BAD_REQUEST, ResponseMessage.NOT_FOUND, "Health facility not found!");
        }

        healthFacilityRepository.delete(optionalHealthFacility.get());
    }

    public HealthFacilityRequest update(Long id, HealthFacilityRequest request) throws BusinessFlowException {
        Optional<HealthFacility> optionalHealthFacility = healthFacilityRepository.findById(id);
        if (optionalHealthFacility.isEmpty()) {
            throw new BusinessFlowException(HttpStatus.BAD_REQUEST, ResponseMessage.NOT_FOUND, "Health facility not found!");
        }

        Optional<City> optionalCity = cityRepository.findById(request.getCity().getId());
        if (optionalCity.isEmpty()) {
            throw new BusinessFlowException(HttpStatus.BAD_REQUEST, ResponseMessage.BAD_REQUEST, "City not found!");
        }

        Optional<Admin> optionalAdmin = adminRepository.findById(request.getAdmin().getId());
        if (optionalAdmin.isEmpty()) {
            throw new BusinessFlowException(HttpStatus.BAD_REQUEST, ResponseMessage.BAD_REQUEST, "Admin not found!");
        }

        City city = optionalCity.get();
        Admin admin = optionalAdmin.get();

        HealthFacility healthFacility = optionalHealthFacility.get();
        healthFacility.setAdmin(optionalAdmin.get());
        healthFacility.setCity(optionalCity.get());
        healthFacility.setName(request.getName());
        healthFacilityRepository.save(healthFacility);

        return HealthFacilityRequest.builder()
                .id(healthFacility.getId())
                .name(healthFacility.getName())
                .admin(AdminsRequest.builder()
                        .id(admin.getId())
                        .name(admin.getName())
                        .build())
                .city(CityRequest.builder()
                        .id(city.getId())
                        .name(city.getName())
                        .build())
                .build();
    }

    public HealthFacilityRequest find(Long id) {
        Optional<HealthFacility> optionalHealthFacility = healthFacilityRepository.findById(id);
        if (optionalHealthFacility.isEmpty()) {
            throw new BusinessFlowException(HttpStatus.BAD_REQUEST, ResponseMessage.NOT_FOUND, "Health facility not found!");
        }
        HealthFacility healthFacility = optionalHealthFacility.get();
        City city = healthFacility.getCity();
        Admin admin = healthFacility.getAdmin();
        return HealthFacilityRequest
                .builder()
                .id(healthFacility.getId())
                .name(healthFacility.getName())
                .city(CityRequest.builder()
                        .id(city.getId())
                        .name(city.getName())
                        .build())
                .admin(AdminsRequest.builder()
                        .id(admin.getId())
                        .name(admin.getName())
                        .build())
                .build();
    }

    public List<HealthFacilityRequest> find() {
        List<HealthFacility> healthFacilityList = healthFacilityRepository.findAll();
        List<HealthFacilityRequest> healthFacilityRequests = new ArrayList<>();
        for(HealthFacility healthFacility : healthFacilityList) {
            City city = healthFacility.getCity();
            Admin admin = healthFacility.getAdmin();

            healthFacilityRequests.add(HealthFacilityRequest
                    .builder()
                    .id(healthFacility.getId())
                    .name(healthFacility.getName())
                    .city(CityRequest.builder()
                            .id(city.getId())
                            .name(city.getName())
                            .build())
                    .admin(AdminsRequest.builder()
                            .id(admin.getId())
                            .name(admin.getName())
                            .build())
                    .build());
        }
        return healthFacilityRequests;
    }
}