package com.evizy.evizy.service;

import com.evizy.evizy.config.JwtTokenProvider;
import com.evizy.evizy.constant.ResponseMessage;
import com.evizy.evizy.domain.common.ApiResponse;
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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = AuthService.class)
class AuthServiceTest {
    @MockBean
    private UsersRepository usersRepository;

    @MockBean
    private AdminRepository adminRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private UsersService usersService;

    @Autowired
    private AuthService authService;

    @Test
    void registerUsersSuccess_Test() {
        when(usersService.getAllCitizen()).thenReturn(List.of(
                CitizenResponse.builder()
                        .id(1L)
                        .nik("1234567890123456")
                        .name("Nathan Ramli")
                        .build()
        ));

        when(usersRepository.getDistinctTopByNik("1234567890123456")).thenReturn(null);
        when(usersRepository.save(any())).thenReturn(Users
                .builder()
                .id(1L)
                .nik("1234567890123456")
                .password("123456")
                .name("Nathan Ramli")
                .build());

        Users user = authService.register(
                UsersRequest
                        .builder()
                        .nik("1234567890123456")
                        .password("123456")
                        .name("Nathan Ramli")
                        .build()
        );
        assertEquals(1L, user.getId());
        assertEquals("1234567890123456", user.getNik());
        assertEquals("Nathan Ramli", user.getName());
        assertEquals("123456", user.getPassword());
    }

    @Test
    void registerUsersErrorExist_Test() {
        when(usersRepository.getDistinctTopByNik("1234567890123456")).thenReturn(Users
                .builder()
                .id(1L)
                .nik("1234567890123456")
                .password("123456")
                .name("Nathan Ramli")
                .build());

        try {
            Users user = authService.register(
                    UsersRequest
                            .builder()
                            .nik("1234567890123456")
                            .password("123456")
                            .name("Nathan Ramli")
                            .build()
            );
            fail();
        } catch (BusinessFlowException e) {
            assertEquals(ResponseMessage.ALREADY_EXIST, e.getCode());
        }
    }


    @Test
    void registerUsersErrorInvalidNik_Test() {
        when(usersRepository.getDistinctTopByNik(any())).thenReturn(null);
        when(usersService.getAllCitizen()).thenReturn(List.of());

        try {
            Users user = authService.register(
                    UsersRequest
                            .builder()
                            .nik("1234567890123456")
                            .password("123456")
                            .name("Nathan Ramli")
                            .build()
            );
            fail();
        } catch (BusinessFlowException e) {
            assertEquals(ResponseMessage.INVALID_NIK, e.getCode());
        }
    }

    @Test
    void authenticatedAndGenerateTokenSuccess_Test() {
        when(jwtTokenProvider.generateToken(any())).thenReturn("THIS_IS_A_TOKEN");

        ResponseEntity<?> responseEntity = authService.authenticatedAndGenerateToken(
                UsersRequest
                        .builder()
                        .nik("1234567890123456")
                        .password("123456")
                        .name("Nathan Ramli")
                        .build()
        );
        ApiResponse response = (ApiResponse) responseEntity.getBody();
        TokenResponse tokenResponse = (TokenResponse) Objects.requireNonNull(response).getData();
        assertEquals(HttpStatus.OK.value(), responseEntity.getStatusCodeValue());
        assertEquals("THIS_IS_A_TOKEN", tokenResponse.getAccessToken());
    }
}