package com.evizy.evizy.controller;


import com.evizy.evizy.constant.ResponseMessage;
import com.evizy.evizy.domain.dto.*;
import com.evizy.evizy.errors.BusinessFlowException;
import com.evizy.evizy.service.AdminService;
import com.evizy.evizy.service.FamilyMembersService;
import com.evizy.evizy.service.HealthFacilitiesVaccinesService;
import com.evizy.evizy.service.HealthFacilityService;
import com.evizy.evizy.util.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/api/v1/health-facilities")
public class HealthFacilitiesController {
    private final HealthFacilityService healthFacilityService;
    private final HealthFacilitiesVaccinesService healthFacilitiesVaccinesService;
    private final AdminService adminService;
    private final FamilyMembersService familyMembersService;

    @PostMapping("")
    public ResponseEntity<?> create(Principal principal, @RequestBody HealthFacilityRequest request) {
        try {
            AdminsRequest admin = adminService.find(principal.getName());
            if (!admin.isSuperAdmin())
                throw new BusinessFlowException(HttpStatus.UNAUTHORIZED, ResponseMessage.UNAUTHORIZED, "Unauthorized to create new health facility.");

            HealthFacilityRequest newHealthFacility = healthFacilityService.create(request);
            return Response.build(ResponseMessage.SUCCESS, HttpStatus.CREATED, newHealthFacility);
        } catch (BusinessFlowException e) {
            return Response.build(e.getCode(), e.getHttpStatus(), null);
        } catch (Exception e) {
            log.error("Failed to create health facility: {}", e.getMessage());
            log.trace(e);
            return Response.build(ResponseMessage.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(Principal principal, @PathVariable Long id) {
        try {
            AdminsRequest admin = adminService.find(principal.getName());
            if (!admin.isSuperAdmin())
                throw new BusinessFlowException(HttpStatus.UNAUTHORIZED, ResponseMessage.UNAUTHORIZED, "Unauthorized to delete health facility.");

            healthFacilityService.delete(id);
            return Response.build(ResponseMessage.SUCCESS, HttpStatus.OK, null);
        } catch (BusinessFlowException e) {
            return Response.build(e.getCode(), e.getHttpStatus(), null);
        } catch (Exception e) {
            log.error("Failed to delete health facility: {}", e.getMessage());
            log.trace(e);
            return Response.build(ResponseMessage.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(Principal principal, @PathVariable Long id, @RequestBody HealthFacilityRequest request) {
        try {
            AdminsRequest admin = adminService.find(principal.getName());
            if (!admin.isSuperAdmin())
                throw new BusinessFlowException(HttpStatus.UNAUTHORIZED, ResponseMessage.UNAUTHORIZED, "Unauthorized to update health facility.");

            HealthFacilityRequest updatedHealthFacility = healthFacilityService.update(id, request);
            return Response.build(ResponseMessage.SUCCESS, HttpStatus.OK, updatedHealthFacility);
        } catch (BusinessFlowException e) {
            return Response.build(e.getCode(), e.getHttpStatus(), null);
        } catch (Exception e) {
            log.error("Failed to update health facility: {}", e.getMessage());
            log.trace(e);
            return Response.build(ResponseMessage.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        try {
            HealthFacilityRequest healthFacility = healthFacilityService.find(id);
            return Response.build(ResponseMessage.SUCCESS, HttpStatus.OK, healthFacility);
        } catch (BusinessFlowException e) {
            return Response.build(e.getCode(), e.getHttpStatus(), null);
        } catch (Exception e) {
            log.error("Failed to find health facility by id: {}", e.getMessage());
            log.trace(e);
            return Response.build(ResponseMessage.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }

    @GetMapping("")
    public ResponseEntity<?> findAll() {
        try {
            List<HealthFacilityRequest> healthFacilities = healthFacilityService.find();
            return Response.build(ResponseMessage.SUCCESS, HttpStatus.OK, healthFacilities);
        } catch (BusinessFlowException e) {
            return Response.build(e.getCode(), e.getHttpStatus(), null);
        } catch (Exception e) {
            log.error("Failed to get all health facilities: {}", e.getMessage());
            log.trace(e);
            return Response.build(ResponseMessage.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }

    @GetMapping("/{id}/vaccines")
    public ResponseEntity<?> getAllVaccines(@PathVariable Long id) {
        try {
            List<HealthFacilityVaccinesRequest> healthFacilityVaccines = healthFacilitiesVaccinesService.find(id);
            return Response.build(ResponseMessage.SUCCESS, HttpStatus.OK, healthFacilityVaccines);
        } catch (BusinessFlowException e) {
            return Response.build(e.getCode(), e.getHttpStatus(), null);
        } catch (Exception e) {
            log.error("Failed to get all health facilities vaccines: {}", e.getMessage());
            log.trace(e);
            return Response.build(ResponseMessage.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }

    @PostMapping("/{id}/vaccines")
    public ResponseEntity<?> createVaccineDistribution(Principal principal, @PathVariable Long id, @RequestBody VaccineDistributionRequest request) {
        try {
            AdminsRequest admin = adminService.find(principal.getName());

            HealthFacilityRequest healthFacility = healthFacilityService.find(id);
            if (!healthFacility.getAdmin().getId().equals(admin.getId()) && !admin.isSuperAdmin()) {
                throw new BusinessFlowException(HttpStatus.UNAUTHORIZED, ResponseMessage.UNAUTHORIZED, "Unauthorized to create vaccine distribution.");
            }

            request.setHealthFacility(HealthFacilityRequest.builder()
                    .id(id)
                    .build());
            HealthFacilityVaccinesRequest healthFacilityVaccines = healthFacilitiesVaccinesService.distribute(request);
            return Response.build(ResponseMessage.SUCCESS, HttpStatus.OK, healthFacilityVaccines);
        } catch (BusinessFlowException e) {
            return Response.build(e.getCode(), e.getHttpStatus(), null);
        } catch (Exception e) {
            log.error("Failed to create vaccine distribution: {}", e.getMessage());
            log.trace(e);
            return Response.build(ResponseMessage.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }
}