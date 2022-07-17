package com.evizy.evizy.service;

import com.evizy.evizy.constant.ResponseMessage;
import com.evizy.evizy.domain.common.ApiResponse;
import com.evizy.evizy.domain.dao.Admin;
import com.evizy.evizy.domain.dao.Users;
import com.evizy.evizy.domain.dto.CitizenResponse;
import com.evizy.evizy.domain.dto.TokenResponse;
import com.evizy.evizy.domain.dto.UsersRequest;
import com.evizy.evizy.errors.BusinessFlowException;
import com.evizy.evizy.repository.AdminRepository;
import com.evizy.evizy.repository.UsersRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = UsersService.class)
class UsersServiceTest {
    @MockBean
    private UsersRepository usersRepository;

    @MockBean
    private AdminRepository adminRepository;

    @MockBean
    private RestTemplate restTemplate;

    @Autowired
    private UsersService usersService;

    @Test
    void loadAdminByUsernameSuccess_Test() {
        when(adminRepository.getDistinctTopByUsername(any())).thenReturn(Admin.builder()
                .id(1L)
                .name("Admin")
                .username("admin")
                .build());

        Admin admin = (Admin) usersService.loadUserByUsername("admin_admin");
        assertEquals("Admin", admin.getName());
        assertEquals("admin", admin.getUsername());
        assertEquals(1L, admin.getId());
    }

    @Test
    void loadUserByNikSuccess_Test() {
        when(usersRepository.getDistinctTopByNik(any())).thenReturn(Users.builder()
                .id(1L)
                .name("Users")
                .nik("1234567890123456")
                .build());

        Users user = (Users) usersService.loadUserByUsername("user_1234567890123456");
        assertEquals("Users", user.getName());
        assertEquals("1234567890123456", user.getNik());
        assertEquals(1L, user.getId());
    }

    @Test
    void loadUserByNikFail_Test() {
        when(usersRepository.getDistinctTopByNik(any())).thenReturn(null);

        try {
            Users user = (Users) usersService.loadUserByUsername("user_1234567890123456");
            fail();
        } catch (UsernameNotFoundException e) {

        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void getAllCitizenSuccess_Test() {
        when(restTemplate.getForObject(anyString(), any())).thenReturn(
                new CitizenResponse[]{
                        CitizenResponse.builder()
                                .nik("1234567890123456")
                                .name("Warga 1")
                                .build(),
                        CitizenResponse.builder()
                                .nik("1234567890123457")
                                .name("Warga 2")
                                .build()
                }
        );
        List<CitizenResponse> citizenResponses = usersService.getAllCitizen();
        assertEquals("1234567890123456", citizenResponses.get(0).getNik());
        assertEquals("Warga 1", citizenResponses.get(0).getName());
        assertEquals("1234567890123457", citizenResponses.get(1).getNik());
        assertEquals("Warga 2", citizenResponses.get(1).getName());
    }

    @Test
    void findAllUserSuccess_Test() {
        when(usersRepository.findAll()).thenReturn(List.of(
                Users.builder()
                        .nik("1234567890123456")
                        .name("User 1")
                        .build(),
                Users.builder()
                        .nik("1234567890123457")
                        .name("User 2")
                        .build()
        ));

        List<UsersRequest> users = usersService.find();
        assertEquals("1234567890123456", users.get(0).getNik());
        assertEquals("User 1", users.get(0).getName());
        assertEquals("1234567890123457", users.get(1).getNik());
        assertEquals("User 2", users.get(1).getName());
    }

    @Test
    void findUserByIdSuccess_Test() {
        when(usersRepository.findById(any())).thenReturn(Optional.of(
                Users.builder()
                        .id(1L)
                        .nik("1234567890123456")
                        .name("User 1")
                        .build()
        ));

        UsersRequest user = usersService.find(1L);
        assertEquals("1234567890123456", user.getNik());
        assertEquals("User 1", user.getName());
        assertEquals(1L, user.getId());
    }

    @Test
    void findUserByIdFail_Test() {
        when(usersRepository.findById(any())).thenReturn(Optional.empty());

        try {
            UsersRequest user = usersService.find(1L);
            fail();
        } catch (BusinessFlowException e) {
        } catch (Exception e) {
            fail();
        }
    }
}