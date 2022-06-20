package com.evizy.evizy.service;

import com.evizy.evizy.config.JwtTokenProvider;
import com.evizy.evizy.constant.ResponseMessage;
import com.evizy.evizy.domain.dao.Admin;
import com.evizy.evizy.domain.dao.Users;
import com.evizy.evizy.domain.dto.AdminsRequest;
import com.evizy.evizy.domain.dto.CitizenResponse;
import com.evizy.evizy.domain.dto.TokenResponse;
import com.evizy.evizy.domain.dto.UsersRequest;
import com.evizy.evizy.errors.BusinessFlowException;
import com.evizy.evizy.repository.AdminRepository;
import com.evizy.evizy.repository.UsersRepository;
import com.evizy.evizy.util.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {
    private final AdminRepository adminRepository;

    private final PasswordEncoder passwordEncoder;

    public AdminsRequest create(AdminsRequest adminsRequest) throws BusinessFlowException {
        Admin exists = adminRepository.getDistinctTopByUsername(adminsRequest.getUsername());
        if (exists != null) {
            throw new BusinessFlowException(HttpStatus.BAD_REQUEST, ResponseMessage.ALREADY_EXIST, "Admin already exist!");
        }

        Admin admin = Admin.builder()
                .username(adminsRequest.getUsername())
                .name(adminsRequest.getName())
                .isSuperAdmin(false)
                .active(true)
                .password(passwordEncoder.encode(adminsRequest.getPassword())).build();
        adminRepository.save(admin);
        return AdminsRequest.builder()
                .id(admin.getId())
                .name(admin.getName())
                .username(admin.getName())
                .build();
    }

    public void delete(Long id) throws BusinessFlowException {
        Admin admin = adminRepository.getById(id);
        if (admin == null) {
            throw new BusinessFlowException(HttpStatus.BAD_REQUEST, ResponseMessage.NOT_FOUND, "Admin not found!");
        }

        adminRepository.delete(admin);
    }

    public AdminsRequest update(Long id, AdminsRequest adminsRequest) throws BusinessFlowException {
        Admin admin = adminRepository.getById(id);
        if (admin == null) {
            throw new BusinessFlowException(HttpStatus.BAD_REQUEST, ResponseMessage.NOT_FOUND, "Admin not found!");
        }

        Admin exists = adminRepository.getDistinctTopByUsername(adminsRequest.getUsername());
        if (!admin.getUsername().equalsIgnoreCase(adminsRequest.getUsername()) && exists != null) {
            throw new BusinessFlowException(HttpStatus.BAD_REQUEST, ResponseMessage.ALREADY_EXIST, "Admin already exist!");
        }

        admin.setUsername(adminsRequest.getUsername());
        admin.setName(adminsRequest.getName());
        admin.setPassword(passwordEncoder.encode(adminsRequest.getPassword()));
        adminRepository.save(admin);

        return AdminsRequest.builder()
                .id(admin.getId())
                .name(admin.getName())
                .username(admin.getName())
                .build();
    }

    public AdminsRequest find(String username) {
        Admin admin = adminRepository.findByUsername(username);
        if (admin == null)
            throw new BusinessFlowException(HttpStatus.BAD_REQUEST, ResponseMessage.BAD_REQUEST, "Admin not found!");
        return AdminsRequest
                .builder()
                .id(admin.getId())
                .username(admin.getUsername())
                .isSuperAdmin(admin.isSuperAdmin())
                .name(admin.getName())
                .build();
    }

    public AdminsRequest find(Long id) {
        Optional<Admin> optionalAdmin = adminRepository.findById(id);
        if (optionalAdmin.isEmpty())
            throw new BusinessFlowException(HttpStatus.BAD_REQUEST, ResponseMessage.BAD_REQUEST, "Admin not found!");
        Admin admin = optionalAdmin.get();
        return AdminsRequest
                .builder()
                .id(admin.getId())
                .username(admin.getUsername())
                .isSuperAdmin(admin.isSuperAdmin())
                .name(admin.getName())
                .build();
    }

    public List<AdminsRequest> find() {
        List<Admin> adminList = adminRepository.findAll();
        List<AdminsRequest> adminsRequests = new ArrayList<>();
        for(Admin admin :adminList) {
            adminsRequests.add(AdminsRequest
                    .builder()
                    .id(admin.getId())
                    .username(admin.getUsername())
                    .name(admin.getName())
                    .isSuperAdmin(admin.isSuperAdmin())
                    .build());
        }
        return adminsRequests;
    }
}
