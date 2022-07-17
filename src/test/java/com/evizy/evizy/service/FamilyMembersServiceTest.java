package com.evizy.evizy.service;

import com.evizy.evizy.constant.ResponseMessage;
import com.evizy.evizy.domain.dao.City;
import com.evizy.evizy.domain.dao.FamilyMembers;
import com.evizy.evizy.domain.dao.Users;
import com.evizy.evizy.domain.dto.CitizenResponse;
import com.evizy.evizy.domain.dto.CityRequest;
import com.evizy.evizy.domain.dto.FamilyMembersRequest;
import com.evizy.evizy.domain.dto.UsersRequest;
import com.evizy.evizy.errors.BusinessFlowException;
import com.evizy.evizy.repository.CityRepository;
import com.evizy.evizy.repository.FamilyMembersRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = FamilyMembersService.class)
class FamilyMembersServiceTest {
    @MockBean
    private FamilyMembersRepository familyMembersRepository;
    @MockBean
    private UsersService usersService;
    @Autowired
    private FamilyMembersService familyMembersService;

    @Test
    void createFamilyMemberSuccess_Test() {
        when(usersService.getAllCitizen()).thenReturn(List.of(
                CitizenResponse.builder()
                       .nik("1234567890123456")
                       .name("Warga 1")
                       .build(),
                CitizenResponse.builder()
                        .nik("1234567890123457")
                        .name("Warga 2")
                        .build()
        ));

        when(familyMembersRepository.save(any())).thenAnswer(i -> {
            ((FamilyMembers) i.getArgument(0)).setId(1L);
            return null;
        });

        FamilyMembersRequest familyMembersRequest = familyMembersService.create(FamilyMembersRequest.builder()
                .user(UsersRequest.builder()
                        .id(1L)
                        .build())
                .nik("1234567890123456")
                .phoneNumber("081234567890")
                .relationship("Sister")
                .dateOfBirth(LocalDate.of(2022, 12, 25))
                .gender('F')
                .name("Jakarta")
                .build());
        assertEquals(1L, familyMembersRequest.getId());
        assertEquals("1234567890123456", familyMembersRequest.getNik());
    }

    @Test
    void createFamilyMemberFail_Test() {
        when(usersService.getAllCitizen()).thenReturn(List.of(
                CitizenResponse.builder()
                        .nik("1234567890123456")
                        .name("Warga 1")
                        .build(),
                CitizenResponse.builder()
                        .nik("1234567890123457")
                        .name("Warga 2")
                        .build()
        ));

        try {
            FamilyMembersRequest familyMembersRequest = familyMembersService.create(FamilyMembersRequest.builder()
                    .user(UsersRequest.builder()
                            .id(1L)
                            .build())
                    .nik("1234567890123458")
                    .phoneNumber("081234567890")
                    .relationship("Sister")
                    .dateOfBirth(LocalDate.of(2022, 12, 25))
                    .gender('F')
                    .name("Jakarta")
                    .build());
            fail();
        } catch (BusinessFlowException e) {
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void deleteFamilyMemberFail_Test() {
        when(familyMembersRepository.findById(any())).thenReturn(Optional.empty());
        doNothing().when(familyMembersRepository).delete(any());

        try {
            familyMembersService.delete(1L);
            fail();
        } catch (BusinessFlowException e) {
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void deleteFamilyMemberSuccess_Test() {
        when(familyMembersRepository.findById(any())).thenReturn(Optional.of(FamilyMembers.builder()
                .id(1L)
                .name("Family Member 1")
                .build()));
        doNothing().when(familyMembersRepository).delete(any());
        familyMembersService.delete(1L);
    }

    @Test
    void findAllFamilyMembersByUserIdSuccess_Test() {
        when(familyMembersRepository.findAllByUsersId(any())).thenReturn(List.of(
                FamilyMembers.builder()
                        .id(1L)
                        .users(Users.builder()
                                .id(1L)
                                .build())
                        .name("Family Member 1")
                        .build(),
                FamilyMembers.builder()
                        .id(2L)
                        .users(Users.builder()
                                .id(1L)
                                .build())
                        .name("Family Member 2")
                        .build()
        ));

        List<FamilyMembersRequest> familyMembers = familyMembersService.findAll(1L);
        assertEquals(1L, familyMembers.get(0).getId());
        assertEquals("Family Member 1", familyMembers.get(0).getName());
        assertEquals(2L, familyMembers.get(1).getId());
        assertEquals("Family Member 2", familyMembers.get(1).getName());
    }

    @Test
    void findAllFamilyMembersSuccess_Test() {
        when(familyMembersRepository.findAll()).thenReturn(List.of(
                FamilyMembers.builder()
                        .id(1L)
                        .users(Users.builder()
                                .id(1L)
                                .build())
                        .name("Family Member 1")
                        .build(),
                FamilyMembers.builder()
                        .id(2L)
                        .users(Users.builder()
                                .id(2L)
                                .build())
                        .name("Family Member 2")
                        .build()
        ));

        List<FamilyMembersRequest> familyMembers = familyMembersService.findAll(null);
        assertEquals(1L, familyMembers.get(0).getId());
        assertEquals("Family Member 1", familyMembers.get(0).getName());
        assertEquals(2L, familyMembers.get(1).getId());
        assertEquals("Family Member 2", familyMembers.get(1).getName());
    }

    @Test
    void findFamilyMemberByIdSuccess_Test() {
        when(familyMembersRepository.findById(any())).thenReturn(Optional.of(
                FamilyMembers.builder()
                        .id(1L)
                        .users(Users.builder()
                                .id(1L)
                                .build())
                        .name("Family Member 1")
                        .build()
        ));

        FamilyMembersRequest familyMembersRequest = familyMembersService.find(1L);
        assertEquals("Family Member 1", familyMembersRequest.getName());
        assertEquals(1L, familyMembersRequest.getId());
    }

    @Test
    void findFamilyMemberByIdFail_Test() {
        when(familyMembersRepository.findById(any())).thenReturn(Optional.empty());

        try {
            FamilyMembersRequest familyMembersRequest = familyMembersService.find(1L);
            fail();
        } catch (BusinessFlowException e) {
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void updateFamilyMemberNotFoundFail_Test() {
        when(familyMembersRepository.findById(any())).thenReturn(Optional.empty());

        try {
            FamilyMembersRequest familyMembersRequest = familyMembersService.update(1L, FamilyMembersRequest.builder()
                    .name("New Name")
                    .build());
            fail();
        } catch (BusinessFlowException e) {

        } catch (Exception e){
            fail();
        }
    }

    @Test
    void updateFamilyMemberInvalidNikFail_Test() {
        when(familyMembersRepository.findById(any())).thenReturn(Optional.of(
                FamilyMembers.builder()
                        .id(1L)
                        .users(Users.builder()
                                .id(1L)
                                .build())
                        .name("Family Member 1")
                        .build()
        ));
        when(usersService.getAllCitizen()).thenReturn(List.of(
                CitizenResponse.builder()
                        .nik("1234567890123456")
                        .name("Warga 1")
                        .build(),
                CitizenResponse.builder()
                        .nik("1234567890123457")
                        .name("Warga 2")
                        .build()
        ));

        try {
            FamilyMembersRequest familyMembersRequest = familyMembersService.update(1L, FamilyMembersRequest.builder()
                    .nik("1234567890123458")
                    .name("New Name")
                    .build());
            fail();
        } catch (BusinessFlowException e) {

        } catch (Exception e){
            fail();
        }
    }

    @Test
    void updateFamilyMemberSuccess_Test() {
        when(familyMembersRepository.findById(any())).thenReturn(Optional.of(
                FamilyMembers.builder()
                        .id(1L)
                        .nik("1234567890123456")
                        .users(Users.builder()
                                .id(1L)
                                .build())
                        .name("Family Member 1")
                        .build()
        ));
        when(usersService.getAllCitizen()).thenReturn(List.of(
                CitizenResponse.builder()
                        .nik("1234567890123456")
                        .name("Warga 1")
                        .build(),
                CitizenResponse.builder()
                        .nik("1234567890123457")
                        .name("Warga 2")
                        .build()
        ));

        FamilyMembersRequest familyMembersRequest = familyMembersService.update(1L, FamilyMembersRequest.builder()
                .nik("1234567890123457")
                .name("New Name")
                .build());
        assertEquals("1234567890123457", familyMembersRequest.getNik());
        assertEquals(1L, familyMembersRequest.getId());
    }
}