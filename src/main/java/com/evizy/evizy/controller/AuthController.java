package com.evizy.evizy.controller;

import com.evizy.evizy.constant.ResponseMessage;
import com.evizy.evizy.domain.dao.Users;
import com.evizy.evizy.domain.dto.UsersRequest;
import com.evizy.evizy.errors.BusinessFlowException;
import com.evizy.evizy.service.AuthService;
import com.evizy.evizy.util.Response;
import com.evizy.evizy.util.Validation;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import javax.validation.ConstraintViolationException;

@RestController
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UsersRequest request) {
        try {
            Validation.validate(request);

            request.setNik("user_" + request.getNik());
            Users registeredUser = authService.register(request);
            return authService.authenticatedAndGenerateToken(request);
        } catch (ConstraintViolationException e) {
            return Response.build(ResponseMessage.INVALID_INPUT, HttpStatus.BAD_REQUEST, null);
        } catch (BusinessFlowException e) {
            return Response.build(e.getCode(), e.getHttpStatus(), null);
        } catch (Exception e) {
            log.error("Failed to register user: {}", e.getMessage());
            log.trace(e);
            return Response.build(ResponseMessage.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UsersRequest request) {
        try {
            request.setNik("user_" + request.getNik());
            return authService.authenticatedAndGenerateToken(request);
        } catch (BadCredentialsException e) {
            return Response.build(ResponseMessage.INVALID_CREDENTIALS, HttpStatus.BAD_REQUEST, null);
        } catch (Exception e) {
            log.error("User failed to login: {}", e.getMessage());
            log.trace(e);
            return Response.build(ResponseMessage.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }
}