package com.evizy.evizy.controller;

import com.evizy.evizy.constant.ResponseMessage;
import com.evizy.evizy.domain.dao.Admin;
import com.evizy.evizy.domain.dto.VaccineRequest;
import com.evizy.evizy.errors.BusinessFlowException;
import com.evizy.evizy.service.AuthService;
import com.evizy.evizy.service.VaccineService;
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
@RequestMapping("/api/v1/vaccines")
public class VaccineController {
    private final VaccineService vaccineService;
    private final AuthService authService;

    @PostMapping("")
    public ResponseEntity<?> create(Principal principal, @RequestBody VaccineRequest request) {
        try {
            Admin admin = (Admin) authService.getInfoByPrincipal("admin_" + principal.getName());
            if (!admin.isSuperAdmin())
                throw new BusinessFlowException(HttpStatus.UNAUTHORIZED, ResponseMessage.UNAUTHORIZED, "Unauthorized to create new vaccine.");

            Validation.validate(request);

            VaccineRequest newVaccine = vaccineService.create(request);
            return Response.build(ResponseMessage.SUCCESS, HttpStatus.CREATED, newVaccine);
        } catch (ConstraintViolationException e) {
            return Response.build(ResponseMessage.INVALID_INPUT, HttpStatus.BAD_REQUEST, null);
        } catch (BusinessFlowException e) {
            return Response.build(e.getCode(), e.getHttpStatus(), null);
        } catch (Exception e) {
            log.error("Failed to create vaccine: {}", e.getMessage());
            log.trace(e);
            return Response.build(ResponseMessage.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(Principal principal, @PathVariable Long id) {
        try {
            Admin admin = (Admin) authService.getInfoByPrincipal("admin_" + principal.getName());
            if (!admin.isSuperAdmin())
                throw new BusinessFlowException(HttpStatus.UNAUTHORIZED, ResponseMessage.UNAUTHORIZED, "Unauthorized to delete vaccine.");

            vaccineService.delete(id);
            return Response.build(ResponseMessage.SUCCESS, HttpStatus.OK, null);
        } catch (BusinessFlowException e) {
            return Response.build(e.getCode(), e.getHttpStatus(), null);
        } catch (Exception e) {
            log.error("Failed to delete vaccine: {}", e.getMessage());
            log.trace(e);
            return Response.build(ResponseMessage.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(Principal principal, @PathVariable Long id, @RequestBody VaccineRequest request) {
        try {
            Admin admin = (Admin) authService.getInfoByPrincipal("admin_" + principal.getName());
            if (!admin.isSuperAdmin())
                throw new BusinessFlowException(HttpStatus.UNAUTHORIZED, ResponseMessage.UNAUTHORIZED, "Unauthorized to update vaccine.");

            Validation.validate(request);

            VaccineRequest updatedVaccine = vaccineService.update(id, request);
            return Response.build(ResponseMessage.SUCCESS, HttpStatus.OK, updatedVaccine);
        } catch (ConstraintViolationException e) {
            return Response.build(ResponseMessage.INVALID_INPUT, HttpStatus.BAD_REQUEST, null);
        } catch (BusinessFlowException e) {
            return Response.build(e.getCode(), e.getHttpStatus(), null);
        } catch (Exception e) {
            log.error("Failed to update vaccine: {}", e.getMessage());
            log.trace(e);
            return Response.build(ResponseMessage.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        try {
            VaccineRequest vaccine = vaccineService.find(id);
            return Response.build(ResponseMessage.SUCCESS, HttpStatus.OK, vaccine);
        } catch (BusinessFlowException e) {
            return Response.build(e.getCode(), e.getHttpStatus(), null);
        } catch (Exception e) {
            log.error("Failed to find vaccine by id: {}", e.getMessage());
            log.trace(e);
            return Response.build(ResponseMessage.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }

    @GetMapping("")
    public ResponseEntity<?> findAll() {
        try {
            List<VaccineRequest> vaccines = vaccineService.find();
            return Response.build(ResponseMessage.SUCCESS, HttpStatus.OK, vaccines);
        } catch (BusinessFlowException e) {
            return Response.build(e.getCode(), e.getHttpStatus(), null);
        } catch (Exception e) {
            log.error("Failed to get all vaccines: {}", e.getMessage());
            log.trace(e);
            return Response.build(ResponseMessage.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }
}
