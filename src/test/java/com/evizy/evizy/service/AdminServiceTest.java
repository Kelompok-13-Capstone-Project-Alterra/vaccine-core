package com.evizy.evizy.service;

import com.evizy.evizy.domain.dao.Admin;
import com.evizy.evizy.domain.dao.Users;
import com.evizy.evizy.domain.dto.AdminsRequest;
import com.evizy.evizy.domain.dto.UsersRequest;
import com.evizy.evizy.errors.BusinessFlowException;
import com.evizy.evizy.repository.AdminRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = AdminService.class)
class AdminServiceTest {
    @MockBean
    private AdminRepository adminRepository;
    @MockBean
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AdminService adminService;

    @Test
    void createAdminSuccess_Test() {
        when(adminRepository.getDistinctTopByUsername(any())).thenReturn(null);
        when(adminRepository.save(any())).thenAnswer(i -> {
            ((Admin) i.getArgument(0)).setId(1L);
            return null;
        });

        AdminsRequest adminsRequest = adminService.create(AdminsRequest.builder()
                .username("admin")
                .name("Admin")
                .password("123456")
                .build());
        assertEquals(1L, adminsRequest.getId());
        assertEquals("admin", adminsRequest.getUsername());
        assertEquals("Admin", adminsRequest.getName());
    }

    @Test
    void createAdminFail_Test() {
        when(adminRepository.getDistinctTopByUsername(any())).thenReturn(Admin.builder()
                .id(1L)
                .username("admin")
                .build());

        try {
            AdminsRequest adminsRequest = adminService.create(AdminsRequest.builder()
                    .username("admin")
                    .name("Admin")
                    .password("123456")
                    .build());
            fail();
        } catch (BusinessFlowException e) {

        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void deleteAdminFail_Test() {
        when(adminRepository.getById(any())).thenReturn(null);
        doNothing().when(adminRepository).delete(any());

        try {
            adminService.delete(1L);
            fail();
        } catch (BusinessFlowException e) {
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void deleteAdminSuccess_Test() {
        when(adminRepository.getById(any())).thenReturn(Admin.builder()
                .id(1L)
                .username("admin")
                .build());
        doNothing().when(adminRepository).delete(any());
        adminService.delete(1L);
    }

    @Test
    void findAllAdminSuccess_Test() {
        when(adminRepository.findAll()).thenReturn(List.of(
                Admin.builder()
                        .username("admin")
                        .name("Admin 1")
                        .build(),
                Admin.builder()
                        .username("admin2")
                        .name("Admin 2")
                        .build()
        ));

        List<AdminsRequest> admins = adminService.find();
        assertEquals("admin", admins.get(0).getUsername());
        assertEquals("Admin 1", admins.get(0).getName());
        assertEquals("admin2", admins.get(1).getUsername());
        assertEquals("Admin 2", admins.get(1).getName());
    }

    @Test
    void findAdminByUsernameSuccess_Test() {
        when(adminRepository.findByUsername(any())).thenReturn(
                Admin.builder()
                        .id(1L)
                        .username("admin")
                        .name("Admin")
                        .build()
        );

        AdminsRequest admin = adminService.find("admin");
        assertEquals("admin", admin.getUsername());
        assertEquals("Admin", admin.getName());
        assertEquals(1L, admin.getId());
    }

    @Test
    void findAdminByUsernameFail_Test() {
        when(adminRepository.findByUsername(any())).thenReturn(null);

        try {
            AdminsRequest admin = adminService.find("admin");
            fail();
        } catch (BusinessFlowException e) {
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void findAdminByIdSuccess_Test() {
        when(adminRepository.findById(any())).thenReturn(Optional.of(
                Admin.builder()
                        .id(1L)
                        .username("admin")
                        .name("Admin")
                        .build()
        ));

        AdminsRequest admin = adminService.find(1L);
        assertEquals("admin", admin.getUsername());
        assertEquals("Admin", admin.getName());
        assertEquals(1L, admin.getId());
    }

    @Test
    void findAdminByIdFail_Test() {
        when(adminRepository.findById(any())).thenReturn(Optional.empty());

        try {
            AdminsRequest admin = adminService.find(1L);
            fail();
        } catch (BusinessFlowException e) {
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void updateAdminNotFoundFail_Test() {
        when(adminRepository.getById(any())).thenReturn(null);

        try {
            AdminsRequest adminsRequest = adminService.update(1L, AdminsRequest.builder()
                    .username("admin")
                    .name("Admin")
                    .password("123456")
                    .build());
            fail();
        } catch (BusinessFlowException e) {

        } catch (Exception e){
            fail();
        }
    }

    @Test
    void updateAdminDuplicateUsernameFail_Test() {
        when(adminRepository.getById(any())).thenReturn(Admin.builder()
                .id(1L)
                .username("admin1")
                .name("Admin")
                .password("123456")
                .build());
        when(adminRepository.getDistinctTopByUsername(any())).thenReturn(Admin.builder()
                .id(2L)
                .username("admin2")
                .build());

        try {
            AdminsRequest adminsRequest = adminService.update(1L, AdminsRequest.builder()
                    .username("admin2")
                    .name("Admin")
                    .password("123456")
                    .build());
            fail();
        } catch (BusinessFlowException e) {
        } catch (Exception e){
            fail();
        }
    }

    @Test
    void updateAdminSuccess_Test() {
        when(adminRepository.getById(any())).thenReturn(Admin.builder()
                .id(1L)
                .username("admin1")
                .name("Admin")
                .password("123456")
                .build());
        when(adminRepository.getDistinctTopByUsername(any())).thenReturn(null);

        AdminsRequest adminsRequest = adminService.update(1L, AdminsRequest.builder()
                .username("admin")
                .name("Admin")
                .password("123456")
                .build());
        assertEquals("admin", adminsRequest.getUsername());
        assertEquals("Admin", adminsRequest.getName());
        assertEquals(1L, adminsRequest.getId());
    }
}