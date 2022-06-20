package com.evizy.evizy.controller;

import com.evizy.evizy.constant.ResponseMessage;
import com.evizy.evizy.domain.dao.Admin;
import com.evizy.evizy.domain.dto.AdminsRequest;
import com.evizy.evizy.domain.dto.UsersRequest;
import com.evizy.evizy.errors.BusinessFlowException;
import com.evizy.evizy.service.AdminService;
import com.evizy.evizy.service.AuthService;
import com.evizy.evizy.service.UsersService;
import com.evizy.evizy.util.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin")
public class AdminController {
    private final AdminService adminService;
    private final AuthService authService;

    @PostMapping("")
    public ResponseEntity<?> create(Principal principal, @RequestBody AdminsRequest request) {
        try {
            AdminsRequest admin = adminService.find(principal.getName());
            if (!admin.isSuperAdmin())
                throw new BusinessFlowException(HttpStatus.UNAUTHORIZED, ResponseMessage.UNAUTHORIZED, "Unauthorized to create new admin.");

            AdminsRequest newAdmin = adminService.create(request);
            return Response.build(ResponseMessage.SUCCESS, HttpStatus.CREATED, newAdmin);
        } catch (BusinessFlowException e) {
            return Response.build(e.getCode(), e.getHttpStatus(), null);
        } catch (Exception e) {
            log.error("Failed to register admin: {}", e.getMessage());
            log.trace(e);
            return Response.build(ResponseMessage.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(Principal principal, @PathVariable Long id) {
        try {
            AdminsRequest admin = adminService.find(principal.getName());
            if (!admin.isSuperAdmin())
                throw new BusinessFlowException(HttpStatus.UNAUTHORIZED, ResponseMessage.UNAUTHORIZED, "Unauthorized to delete admin.");

            adminService.delete(id);
            return Response.build(ResponseMessage.SUCCESS, HttpStatus.OK, null);
        } catch (BusinessFlowException e) {
            return Response.build(e.getCode(), e.getHttpStatus(), null);
        } catch (Exception e) {
            log.error("Failed to delete admin: {}", e.getMessage());
            log.trace(e);
            return Response.build(ResponseMessage.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(Principal principal, @PathVariable Long id, @RequestBody AdminsRequest request) {
        try {
            AdminsRequest admin = adminService.find(principal.getName());
            if (!admin.isSuperAdmin())
                throw new BusinessFlowException(HttpStatus.UNAUTHORIZED, ResponseMessage.UNAUTHORIZED, "Unauthorized to update admin.");

            AdminsRequest updatedAdmin = adminService.update(id, request);
            return Response.build(ResponseMessage.SUCCESS, HttpStatus.OK, updatedAdmin);
        } catch (BusinessFlowException e) {
            return Response.build(e.getCode(), e.getHttpStatus(), null);
        } catch (Exception e) {
            log.error("Failed to update admin: {}", e.getMessage());
            log.trace(e);
            return Response.build(ResponseMessage.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AdminsRequest request) {
        try {
            request.setUsername("admin_" + request.getUsername());
            return authService.authenticatedAndGenerateToken(UsersRequest.builder()
                    .nik(request.getUsername())
                    .password(request.getPassword())
                    .build());
        } catch (BadCredentialsException e) {
            return Response.build(ResponseMessage.INVALID_CREDENTIALS, HttpStatus.BAD_REQUEST, null);
        } catch (Exception e) {
            log.error("Admin failed to login: {}", e.getMessage());
            log.trace(e);
            return Response.build(ResponseMessage.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        try {
            AdminsRequest admin = adminService.find(id);
            return Response.build(ResponseMessage.SUCCESS, HttpStatus.OK, admin);
        } catch (BusinessFlowException e) {
            return Response.build(e.getCode(), e.getHttpStatus(), null);
        } catch (Exception e) {
            log.error("Failed to find admin by id: {}", e.getMessage());
            log.trace(e);
            return Response.build(ResponseMessage.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }

    @GetMapping("")
    public ResponseEntity<?> findAll() {
        try {
            List<AdminsRequest> admins = adminService.find();
            return Response.build(ResponseMessage.SUCCESS, HttpStatus.OK, admins);
        } catch (BusinessFlowException e) {
            return Response.build(e.getCode(), e.getHttpStatus(), null);
        } catch (Exception e) {
            log.error("Failed to get all admins: {}", e.getMessage());
            log.trace(e);
            return Response.build(ResponseMessage.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }
}