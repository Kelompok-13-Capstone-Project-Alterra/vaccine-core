package com.evizy.evizy.controller;

import com.evizy.evizy.constant.ResponseMessage;
import com.evizy.evizy.domain.dao.Admin;
import com.evizy.evizy.domain.dao.Users;
import com.evizy.evizy.domain.dto.*;
import com.evizy.evizy.errors.BusinessFlowException;
import com.evizy.evizy.service.*;
import com.evizy.evizy.util.Response;
import com.evizy.evizy.util.Validation;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.ConstraintViolationException;
import java.security.Principal;
import java.util.List;

@RestController
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/api/v1/vaccination-pass")
public class VaccinationPassController {
    private final AuthService authService;
    private final HealthFacilityService healthFacilityService;
    private final VaccinationPassService vaccinationPassService;
    private final VaccinationSessionService vaccinationSessionService;

    @PostMapping("")
    public ResponseEntity<?> create(Principal principal, @RequestBody VaccinationPassRequest request) {
        try {
            Users user = (Users) authService.getInfoByPrincipal("user_" + principal.getName());

            request.setRegisteredBy(UsersRequest.builder()
                    .id(user.getId())
                    .build());

            Validation.validate(request);

            VaccinationPassRequest newVaccinationPass = vaccinationPassService.create(request);
            return Response.build(ResponseMessage.SUCCESS, HttpStatus.CREATED, newVaccinationPass);
        } catch (ConstraintViolationException e) {
            return Response.build(ResponseMessage.INVALID_INPUT, HttpStatus.BAD_REQUEST, null);
        } catch (BusinessFlowException e) {
            return Response.build(e.getCode(), e.getHttpStatus(), null);
        } catch (Exception e) {
            log.error("Failed to create vaccination pass: {}", e.getMessage());
            log.trace(e);
            return Response.build(ResponseMessage.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(Principal principal, @PathVariable Long id) {
        try {
            Users user = (Users) authService.getInfoByPrincipal("user_" + principal.getName());

            VaccinationPassRequest vaccinationPass = vaccinationPassService.find(id);
            if (!vaccinationPass.getRegisteredBy().getId().equals(user.getId())) {
                throw new BusinessFlowException(HttpStatus.UNAUTHORIZED, ResponseMessage.UNAUTHORIZED, "User not authorized to delete this vaccination pass.");
            }

            vaccinationPassService.delete(id);
            return Response.build(ResponseMessage.SUCCESS, HttpStatus.OK, null);
        } catch (BusinessFlowException e) {
            return Response.build(e.getCode(), e.getHttpStatus(), null);
        } catch (Exception e) {
            log.error("Failed to delete vaccination pass: {}", e.getMessage());
            log.trace(e);
            return Response.build(ResponseMessage.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(Principal principal, @PathVariable Long id, @RequestBody VaccinationPassRequest request) {
        try {
            Users user = (Users) authService.getInfoByPrincipal("user_" + principal.getName());

            VaccinationPassRequest vaccinationPass = vaccinationPassService.find(id);
            if (!vaccinationPass.getRegisteredBy().getId().equals(user.getId())) {
                throw new BusinessFlowException(HttpStatus.UNAUTHORIZED, ResponseMessage.UNAUTHORIZED, "User not authorized to update this vaccination pass.");
            }

            Validation.validate(request);

            VaccinationPassRequest updatedVaccinationPass = vaccinationPassService.update(id, request);
            return Response.build(ResponseMessage.SUCCESS, HttpStatus.OK, updatedVaccinationPass);
        } catch (ConstraintViolationException e) {
            return Response.build(ResponseMessage.INVALID_INPUT, HttpStatus.BAD_REQUEST, null);
        } catch (BusinessFlowException e) {
            return Response.build(e.getCode(), e.getHttpStatus(), null);
        } catch (Exception e) {
            log.error("Failed to update vaccination pass: {}", e.getMessage());
            log.trace(e);
            return Response.build(ResponseMessage.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }

    @PutMapping("/{id}/vaccinated-status")
    public ResponseEntity<?> updateVaccinatedStatus(Principal principal, @PathVariable Long id, @RequestBody VaccinationPassRequest request) {
        try {
            Admin admin = (Admin) authService.getInfoByPrincipal("admin_" + principal.getName());

            VaccinationPassRequest vaccinationPass = vaccinationPassService.find(id);
            VaccinationSessionRequest vaccinationSession = vaccinationSessionService.find(vaccinationPass.getVaccinationSession().getId());
            HealthFacilityRequest healthFacility = healthFacilityService.find(vaccinationSession.getHealthFacility().getId());
            if (!healthFacility.getAdmin().getId().equals(admin.getId()) && !admin.isSuperAdmin()) {
                throw new BusinessFlowException(HttpStatus.UNAUTHORIZED, ResponseMessage.UNAUTHORIZED, "Unauthorized to update vaccinated status.");
            }

            vaccinationPassService.updateStatusVaccinated(id, request);
            return Response.build(ResponseMessage.SUCCESS, HttpStatus.OK, null);
        } catch (BusinessFlowException e) {
            return Response.build(e.getCode(), e.getHttpStatus(), null);
        } catch (Exception e) {
            log.error("Failed to update vaccinated status: {}", e.getMessage());
            log.trace(e);
            return Response.build(ResponseMessage.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        try {
            VaccinationPassRequest vaccinationPass = vaccinationPassService.find(id);
            return Response.build(ResponseMessage.SUCCESS, HttpStatus.OK, vaccinationPass);
        } catch (BusinessFlowException e) {
            return Response.build(e.getCode(), e.getHttpStatus(), null);
        } catch (Exception e) {
            log.error("Failed to find vaccination pass by id: {}", e.getMessage());
            log.trace(e);
            return Response.build(ResponseMessage.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }

    @GetMapping("")
    public ResponseEntity<?> findAll(@RequestParam(value = "user_id", required = false) Long userId) {
        try {
            List<VaccinationPassRequest> vaccinationPass = vaccinationPassService.findAll(userId);
            return Response.build(ResponseMessage.SUCCESS, HttpStatus.OK, vaccinationPass);
        } catch (BusinessFlowException e) {
            return Response.build(e.getCode(), e.getHttpStatus(), null);
        } catch (Exception e) {
            log.error("Failed to get all vaccination pass: {}", e.getMessage());
            log.trace(e);
            return Response.build(ResponseMessage.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }
}