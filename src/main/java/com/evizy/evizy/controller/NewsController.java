package com.evizy.evizy.controller;

import com.evizy.evizy.constant.ResponseMessage;
import com.evizy.evizy.domain.dao.Admin;
import com.evizy.evizy.domain.dto.AdminsRequest;
import com.evizy.evizy.domain.dto.NewsRequest;
import com.evizy.evizy.errors.BusinessFlowException;
import com.evizy.evizy.service.AuthService;
import com.evizy.evizy.service.NewsService;
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
@RequestMapping("/api/v1/news")
public class NewsController {
    private final NewsService newsService;
    private final AuthService authService;

    @PostMapping("")
    public ResponseEntity<?> create(Principal principal, @RequestBody NewsRequest request) {
        try {
            Admin admin = (Admin) authService.getInfoByPrincipal("admin_" + principal.getName());
            if (!admin.isSuperAdmin())
                throw new BusinessFlowException(HttpStatus.UNAUTHORIZED, ResponseMessage.UNAUTHORIZED, "Unauthorized to create new news.");

            Validation.validate(request);

            request.setAdmin(AdminsRequest.builder()
                    .id(admin.getId())
                    .build());
            NewsRequest newNews = newsService.create(request);
            return Response.build(ResponseMessage.SUCCESS, HttpStatus.CREATED, newNews);
        } catch (ConstraintViolationException e) {
            return Response.build(ResponseMessage.INVALID_INPUT, HttpStatus.BAD_REQUEST, null);
        } catch (BusinessFlowException e) {
            return Response.build(e.getCode(), e.getHttpStatus(), null);
        } catch (Exception e) {
            log.error("Failed to create news: {}", e.getMessage());
            log.trace(e);
            return Response.build(ResponseMessage.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(Principal principal, @PathVariable Long id) {
        try {
            Admin admin = (Admin) authService.getInfoByPrincipal("admin_" + principal.getName());
            if (!admin.isSuperAdmin())
                throw new BusinessFlowException(HttpStatus.UNAUTHORIZED, ResponseMessage.UNAUTHORIZED, "Unauthorized to delete news.");

            newsService.delete(id);
            return Response.build(ResponseMessage.SUCCESS, HttpStatus.OK, null);
        } catch (BusinessFlowException e) {
            return Response.build(e.getCode(), e.getHttpStatus(), null);
        } catch (Exception e) {
            log.error("Failed to delete news: {}", e.getMessage());
            log.trace(e);
            return Response.build(ResponseMessage.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(Principal principal, @PathVariable Long id, @RequestBody NewsRequest request) {
        try {
            Admin admin = (Admin) authService.getInfoByPrincipal("admin_" + principal.getName());
            if (!admin.isSuperAdmin())
                throw new BusinessFlowException(HttpStatus.UNAUTHORIZED, ResponseMessage.UNAUTHORIZED, "Unauthorized to update news.");

            Validation.validate(request);

            NewsRequest updatedNews = newsService.update(id, request);
            return Response.build(ResponseMessage.SUCCESS, HttpStatus.OK, updatedNews);
        } catch (ConstraintViolationException e) {
            return Response.build(ResponseMessage.INVALID_INPUT, HttpStatus.BAD_REQUEST, null);
        } catch (BusinessFlowException e) {
            return Response.build(e.getCode(), e.getHttpStatus(), null);
        } catch (Exception e) {
            log.error("Failed to update news: {}", e.getMessage());
            log.trace(e);
            return Response.build(ResponseMessage.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        try {
            NewsRequest news = newsService.find(id);
            return Response.build(ResponseMessage.SUCCESS, HttpStatus.OK, news);
        } catch (BusinessFlowException e) {
            return Response.build(e.getCode(), e.getHttpStatus(), null);
        } catch (Exception e) {
            log.error("Failed to find news by id: {}", e.getMessage());
            log.trace(e);
            return Response.build(ResponseMessage.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }

    @GetMapping("")
    public ResponseEntity<?> findAll() {
        try {
            List<NewsRequest> news = newsService.find();
            return Response.build(ResponseMessage.SUCCESS, HttpStatus.OK, news);
        } catch (BusinessFlowException e) {
            return Response.build(e.getCode(), e.getHttpStatus(), null);
        } catch (Exception e) {
            log.error("Failed to get all news: {}", e.getMessage());
            log.trace(e);
            return Response.build(ResponseMessage.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }
}
