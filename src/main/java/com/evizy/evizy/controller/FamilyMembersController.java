package com.evizy.evizy.controller;

import com.evizy.evizy.constant.ResponseMessage;
import com.evizy.evizy.domain.dao.Users;
import com.evizy.evizy.domain.dto.FamilyMembersRequest;
import com.evizy.evizy.domain.dto.UsersRequest;
import com.evizy.evizy.errors.BusinessFlowException;
import com.evizy.evizy.service.AuthService;
import com.evizy.evizy.service.FamilyMembersService;
import com.evizy.evizy.service.UsersService;
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
@RequestMapping("/api/v1/family-members")
public class FamilyMembersController {
    private final AuthService authService;
    private final FamilyMembersService familyMembersService;

    @PostMapping("")
    public ResponseEntity<?> create(Principal principal, @RequestBody FamilyMembersRequest request) {
        try {
            Users user = (Users) authService.getInfoByPrincipal("user_" + principal.getName());

            request.setUser(UsersRequest.builder()
                    .id(user.getId())
                    .build());

            Validation.validate(request);

            FamilyMembersRequest newFamilyMember = familyMembersService.create(request);
            return Response.build(ResponseMessage.SUCCESS, HttpStatus.CREATED, newFamilyMember);
        } catch (ConstraintViolationException e) {
            return Response.build(ResponseMessage.INVALID_INPUT, HttpStatus.BAD_REQUEST, null);
        } catch (BusinessFlowException e) {
            return Response.build(e.getCode(), e.getHttpStatus(), null);
        } catch (Exception e) {
            log.error("Failed to create family member: {}", e.getMessage());
            log.trace(e);
            return Response.build(ResponseMessage.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(Principal principal, @PathVariable Long id) {
        try {
            Users user = (Users) authService.getInfoByPrincipal("user_" + principal.getName());
            FamilyMembersRequest familyMembers = familyMembersService.find(id);
            if (!familyMembers.getUser().getId().equals(user.getId())) {
                throw new BusinessFlowException(HttpStatus.UNAUTHORIZED, ResponseMessage.UNAUTHORIZED, "User not authorized to delete this family member.");
            }

            familyMembersService.delete(id);
            return Response.build(ResponseMessage.SUCCESS, HttpStatus.OK, null);
        } catch (BusinessFlowException e) {
            return Response.build(e.getCode(), e.getHttpStatus(), null);
        } catch (Exception e) {
            log.error("Failed to delete family member: {}", e.getMessage());
            log.trace(e);
            return Response.build(ResponseMessage.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(Principal principal, @PathVariable Long id, @RequestBody FamilyMembersRequest request) {
        try {
            Users user = (Users) authService.getInfoByPrincipal("user_" + principal.getName());
            FamilyMembersRequest familyMembers = familyMembersService.find(id);
            if (!familyMembers.getUser().getId().equals(user.getId())) {
                throw new BusinessFlowException(HttpStatus.UNAUTHORIZED, ResponseMessage.UNAUTHORIZED, "User not authorized to update this family member.");
            }

            Validation.validate(request);

            FamilyMembersRequest updatedFamilyMember = familyMembersService.update(id, request);
            return Response.build(ResponseMessage.SUCCESS, HttpStatus.OK, updatedFamilyMember);
        } catch (ConstraintViolationException e) {
            return Response.build(ResponseMessage.INVALID_INPUT, HttpStatus.BAD_REQUEST, null);
        } catch (BusinessFlowException e) {
            return Response.build(e.getCode(), e.getHttpStatus(), null);
        } catch (Exception e) {
            log.error("Failed to update family member: {}", e.getMessage());
            log.trace(e);
            return Response.build(ResponseMessage.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        try {
            FamilyMembersRequest familyMembers = familyMembersService.find(id);
            return Response.build(ResponseMessage.SUCCESS, HttpStatus.OK, familyMembers);
        } catch (BusinessFlowException e) {
            return Response.build(e.getCode(), e.getHttpStatus(), null);
        } catch (Exception e) {
            log.error("Failed to find family member by id: {}", e.getMessage());
            log.trace(e);
            return Response.build(ResponseMessage.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }

    @GetMapping("")
    public ResponseEntity<?> findAll(@RequestParam(value = "user_id", required = false) Long userId) {
        try {
            List<FamilyMembersRequest> familyMembers = familyMembersService.findAll(userId);
            return Response.build(ResponseMessage.SUCCESS, HttpStatus.OK, familyMembers);
        } catch (BusinessFlowException e) {
            return Response.build(e.getCode(), e.getHttpStatus(), null);
        } catch (Exception e) {
            log.error("Failed to get all family members: {}", e.getMessage());
            log.trace(e);
            return Response.build(ResponseMessage.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }
}
