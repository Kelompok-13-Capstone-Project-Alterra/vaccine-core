package com.evizy.evizy.controller;

import com.evizy.evizy.constant.ResponseMessage;
import com.evizy.evizy.domain.dao.Users;
import com.evizy.evizy.domain.dto.UsersRequest;
import com.evizy.evizy.errors.BusinessFlowException;
import com.evizy.evizy.service.UsersService;
import com.evizy.evizy.util.Response;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@Log4j2
@RequestMapping("/api/v1/users")
public class UsersController {
    @Autowired
    private UsersService usersService;

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        try {
            UsersRequest user = usersService.find(id);
            return Response.build(ResponseMessage.SUCCESS, HttpStatus.OK, user);
        } catch (BusinessFlowException e) {
            log.error(e.getMessage());
            return Response.build(e.getCode(), e.getHttpStatus(), null);
        } catch (Exception e) {
            e.printStackTrace();
            return Response.build(ResponseMessage.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }

    @GetMapping("")
    public ResponseEntity<?> findAll() {
        try {
            List<UsersRequest> users = usersService.find();
            return Response.build(ResponseMessage.SUCCESS, HttpStatus.OK, users);
        } catch (BusinessFlowException e) {
            log.error(e.getMessage());
            return Response.build(e.getCode(), e.getHttpStatus(), null);
        } catch (Exception e) {
            e.printStackTrace();
            return Response.build(ResponseMessage.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }
}
