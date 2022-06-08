package com.evizy.evizy.controller;

import com.evizy.evizy.constant.ResponseMessage;
import com.evizy.evizy.domain.dao.Users;
import com.evizy.evizy.domain.dto.UsersRequest;
import com.evizy.evizy.errors.BusinessFlowException;
import com.evizy.evizy.service.AuthService;
import com.evizy.evizy.util.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

@RestController
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UsersRequest request) {
        try {
            Users registeredUser = authService.register(request);
            return authService.authenticatedAndGenerateToken(request);
        } catch (BusinessFlowException e) {
            log.info(e.getMessage());
            return Response.build(e.getCode(), e.getHttpStatus(), null);
        } catch (Exception e) {
            e.printStackTrace();
            return Response.build(ResponseMessage.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UsersRequest request) {
        try {
            return authService.authenticatedAndGenerateToken(request);
        } catch (BadCredentialsException e) {
            return Response.build(ResponseMessage.INVALID_CREDENTIALS, HttpStatus.BAD_REQUEST, null);
        } catch (Exception e) {
            e.printStackTrace();
            return Response.build(ResponseMessage.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }
}