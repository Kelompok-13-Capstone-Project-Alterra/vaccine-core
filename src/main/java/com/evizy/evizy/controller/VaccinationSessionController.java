package com.evizy.evizy.controller;

import com.evizy.evizy.constant.ResponseMessage;
import com.evizy.evizy.domain.dao.Admin;
import com.evizy.evizy.domain.dto.HealthFacilityRequest;
import com.evizy.evizy.domain.dto.VaccinationSessionRequest;
import com.evizy.evizy.errors.BusinessFlowException;
import com.evizy.evizy.service.AuthService;
import com.evizy.evizy.service.HealthFacilityService;
import com.evizy.evizy.service.VaccinationSessionService;
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
@RequestMapping("/api/v1/vaccination-session")
public class VaccinationSessionController {
    private final VaccinationSessionService vaccinationSessionService;
    private final HealthFacilityService healthFacilityService;
    private final AuthService authService;

    @PostMapping("")
    public ResponseEntity<?> create(Principal principal, @RequestBody VaccinationSessionRequest request) {
        try {
            Admin admin = (Admin) authService.getInfoByPrincipal("admin_" + principal.getName());

            HealthFacilityRequest healthFacility = healthFacilityService.find(request.getHealthFacility().getId());
            if (!healthFacility.getAdmin().getId().equals(admin.getId()) && !admin.isSuperAdmin()) {
                throw new BusinessFlowException(HttpStatus.UNAUTHORIZED, ResponseMessage.UNAUTHORIZED, "Unauthorized to create vaccination session.");
            }

            VaccinationSessionRequest newVaccinationSession = vaccinationSessionService.create(request);
            return Response.build(ResponseMessage.SUCCESS, HttpStatus.CREATED, newVaccinationSession);
        } catch (BusinessFlowException e) {
            return Response.build(e.getCode(), e.getHttpStatus(), null);
        } catch (Exception e) {
            log.error("Failed to create vaccination session: {}", e.getMessage());
            log.trace(e);
            return Response.build(ResponseMessage.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(Principal principal, @PathVariable Long id) {
        try {
            Admin admin = (Admin) authService.getInfoByPrincipal("admin_" + principal.getName());

            VaccinationSessionRequest vaccinationSession = vaccinationSessionService.find(id);
            HealthFacilityRequest healthFacility = healthFacilityService.find(vaccinationSession.getHealthFacility().getId());
            if (!healthFacility.getAdmin().getId().equals(admin.getId()) && !admin.isSuperAdmin()) {
                throw new BusinessFlowException(HttpStatus.UNAUTHORIZED, ResponseMessage.UNAUTHORIZED, "Unauthorized to delete vaccination session.");
            }

            vaccinationSessionService.delete(id);
            return Response.build(ResponseMessage.SUCCESS, HttpStatus.OK, null);
        } catch (BusinessFlowException e) {
            return Response.build(e.getCode(), e.getHttpStatus(), null);
        } catch (Exception e) {
            log.error("Failed to delete vaccination session: {}", e.getMessage());
            log.trace(e);
            return Response.build(ResponseMessage.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(Principal principal, @PathVariable Long id, @RequestBody VaccinationSessionRequest request) {
        try {
            Admin admin = (Admin) authService.getInfoByPrincipal("admin_" + principal.getName());

            VaccinationSessionRequest vaccinationSession = vaccinationSessionService.find(id);
            HealthFacilityRequest healthFacility = healthFacilityService.find(vaccinationSession.getHealthFacility().getId());
            if (!healthFacility.getAdmin().getId().equals(admin.getId()) && !admin.isSuperAdmin()) {
                throw new BusinessFlowException(HttpStatus.UNAUTHORIZED, ResponseMessage.UNAUTHORIZED, "Unauthorized to update vaccination session.");
            }

            VaccinationSessionRequest updatedVaccinationSession = vaccinationSessionService.update(id, request);
            return Response.build(ResponseMessage.SUCCESS, HttpStatus.OK, updatedVaccinationSession);
        } catch (BusinessFlowException e) {
            return Response.build(e.getCode(), e.getHttpStatus(), null);
        } catch (Exception e) {
            log.error("Failed to update vaccination session: {}", e.getMessage());
            log.trace(e);
            return Response.build(ResponseMessage.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        try {
            VaccinationSessionRequest vaccinationSession = vaccinationSessionService.find(id);
            return Response.build(ResponseMessage.SUCCESS, HttpStatus.OK, vaccinationSession);
        } catch (BusinessFlowException e) {
            return Response.build(e.getCode(), e.getHttpStatus(), null);
        } catch (Exception e) {
            log.error("Failed to find vaccination session by id: {}", e.getMessage());
            log.trace(e);
            return Response.build(ResponseMessage.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }

    @GetMapping("")
    public ResponseEntity<?> findAll(@RequestParam(value = "city_id", required = false) Long cityId) {
        try {
            List<VaccinationSessionRequest> vaccinationSessions = vaccinationSessionService.findAll(cityId);
            return Response.build(ResponseMessage.SUCCESS, HttpStatus.OK, vaccinationSessions);
        } catch (BusinessFlowException e) {
            return Response.build(e.getCode(), e.getHttpStatus(), null);
        } catch (Exception e) {
            log.error("Failed to get all vaccination sessions: {}", e.getMessage());
            log.trace(e);
            return Response.build(ResponseMessage.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }
}