package com.evizy.evizy.service;

import com.evizy.evizy.domain.dao.*;
import com.evizy.evizy.domain.dto.*;
import com.evizy.evizy.errors.BusinessFlowException;
import com.evizy.evizy.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = VaccinationPassService.class)
class VaccinationPassServiceTest {
    @MockBean
    private VaccinationPassRepository vaccinationPassRepository;
    @MockBean
    private VaccinationSessionsRepository vaccinationSessionsRepository;
    @MockBean
    private FamilyMembersRepository familyMembersRepository;
    @MockBean
    private UsersRepository usersRepository;

    @Autowired
    private VaccinationPassService vaccinationPassService;

    @Test
    void createVaccinationPassSuccess_Test() {
        when(vaccinationSessionsRepository.findById(any())).thenReturn(Optional.of(VaccinationSessions.builder()
                .id(1L)
                .vaccine(Vaccine.builder()
                        .id(1L)
                        .build())
                .quantity(10L)
                .booked(9L)
                .build()));

        when(familyMembersRepository.findById(any())).thenReturn(Optional.of(FamilyMembers.builder()
                .id(1L)
                .users(Users.builder()
                        .id(1L)
                        .build())
                .gender('F')
                .build()));

        when(vaccinationPassRepository.save(any())).thenAnswer(i -> {
            ((VaccinationPass) i.getArgument(0)).setId(1L);
            return null;
        });

        VaccinationPassRequest vaccinationPassRequest = vaccinationPassService.create(VaccinationPassRequest.builder()
                        .familyMember(FamilyMembersRequest.builder()
                                .id(1L)
                                .build())
                        .vaccinationSession(VaccinationSessionRequest.builder()
                                .id(1L)
                                .build())
                        .registeredBy(UsersRequest.builder()
                                .id(1L)
                                .build())
                        .build());
        assertEquals(1L, vaccinationPassRequest.getId());
    }

    @Test
    void createVaccinationPassForUserSuccess_Test() {
        when(vaccinationSessionsRepository.findById(any())).thenReturn(Optional.of(VaccinationSessions.builder()
                .id(1L)
                .vaccine(Vaccine.builder()
                        .id(1L)
                        .build())
                .quantity(10L)
                .booked(9L)
                .build()));

        when(familyMembersRepository.findById(any())).thenReturn(Optional.of(FamilyMembers.builder()
                .id(1L)
                .users(Users.builder()
                        .id(1L)
                        .build())
                .gender('F')
                .build()));

        when(usersRepository.findById(any())).thenReturn(Optional.of(Users.builder()
                .id(1L)
                .gender('F')
                .build()));

        when(vaccinationPassRepository.save(any())).thenAnswer(i -> {
            ((VaccinationPass) i.getArgument(0)).setId(1L);
            return null;
        });

        VaccinationPassRequest vaccinationPassRequest = vaccinationPassService.create(VaccinationPassRequest.builder()
                .vaccinationSession(VaccinationSessionRequest.builder()
                        .id(1L)
                        .build())
                .registeredBy(UsersRequest.builder()
                        .id(1L)
                        .build())
                .build());

        assertEquals(1L, vaccinationPassRequest.getId());
    }

    @Test
    void createVaccinationPassVaccinationSessionNotFoundFail_Test() {
        when(vaccinationSessionsRepository.findById(any())).thenReturn(Optional.empty());

        when(familyMembersRepository.findById(any())).thenReturn(Optional.of(FamilyMembers.builder()
                .id(1L)
                .users(Users.builder()
                        .id(1L)
                        .build())
                .gender('F')
                .build()));

        when(vaccinationPassRepository.save(any())).thenAnswer(i -> {
            ((VaccinationPass) i.getArgument(0)).setId(1L);
            return null;
        });

        try {
            VaccinationPassRequest vaccinationPassRequest = vaccinationPassService.create(VaccinationPassRequest.builder()
                    .familyMember(FamilyMembersRequest.builder()
                            .id(1L)
                            .build())
                    .vaccinationSession(VaccinationSessionRequest.builder()
                            .id(1L)
                            .build())
                    .registeredBy(UsersRequest.builder()
                            .id(1L)
                            .build())
                    .build());
            fail();
        } catch (BusinessFlowException e) {
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void createVaccinationPassStockEmptyFail_Test() {
        when(vaccinationSessionsRepository.findById(any())).thenReturn(Optional.of(VaccinationSessions.builder()
                .id(1L)
                .vaccine(Vaccine.builder()
                        .id(1L)
                        .build())
                .quantity(10L)
                .booked(10L)
                .build()));

        when(familyMembersRepository.findById(any())).thenReturn(Optional.of(FamilyMembers.builder()
                .id(1L)
                .users(Users.builder()
                        .id(1L)
                        .build())
                .gender('F')
                .build()));

        when(vaccinationPassRepository.save(any())).thenAnswer(i -> {
            ((VaccinationPass) i.getArgument(0)).setId(1L);
            return null;
        });

        try {
            VaccinationPassRequest vaccinationPassRequest = vaccinationPassService.create(VaccinationPassRequest.builder()
                    .familyMember(FamilyMembersRequest.builder()
                            .id(1L)
                            .build())
                    .vaccinationSession(VaccinationSessionRequest.builder()
                            .id(1L)
                            .build())
                    .registeredBy(UsersRequest.builder()
                            .id(1L)
                            .build())
                    .build());
            fail();
        } catch (BusinessFlowException e) {
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void createVaccinationPassFamilyMemberNotFoundFail_Test() {
        when(vaccinationSessionsRepository.findById(any())).thenReturn(Optional.of(VaccinationSessions.builder()
                .id(1L)
                .vaccine(Vaccine.builder()
                        .id(1L)
                        .build())
                .quantity(10L)
                .booked(9L)
                .build()));

        when(familyMembersRepository.findById(any())).thenReturn(Optional.empty());

        when(vaccinationPassRepository.save(any())).thenAnswer(i -> {
            ((VaccinationPass) i.getArgument(0)).setId(1L);
            return null;
        });

        try {
            VaccinationPassRequest vaccinationPassRequest = vaccinationPassService.create(VaccinationPassRequest.builder()
                    .familyMember(FamilyMembersRequest.builder()
                            .id(1L)
                            .build())
                    .vaccinationSession(VaccinationSessionRequest.builder()
                            .id(1L)
                            .build())
                    .registeredBy(UsersRequest.builder()
                            .id(1L)
                            .build())
                    .build());
            fail();
        } catch (BusinessFlowException e) {
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void deleteVaccinationPassNotFoundFail_Test() {
        when(vaccinationPassRepository.findById(any())).thenReturn(Optional.empty());
        doNothing().when(vaccinationPassRepository).delete(any());

        try {
            vaccinationPassService.delete(1L);
            fail();
        } catch (BusinessFlowException e) {
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void deleteVaccinationPassAlreadyVaccinatedFail_Test() {
        when(vaccinationPassRepository.findById(any())).thenReturn(Optional.of(
                VaccinationPass.builder()
                        .id(1L)
                        .isVaccinated(true)
                        .build()
        ));
        doNothing().when(vaccinationPassRepository).delete(any());

        try {
            vaccinationPassService.delete(1L);
            fail();
        } catch (BusinessFlowException e) {
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void deleteVaccinationPassSuccess_Test() {
        when(vaccinationPassRepository.findById(any())).thenReturn(Optional.of(
                VaccinationPass.builder()
                        .id(1L)
                        .vaccinationSessions(VaccinationSessions.builder()
                                .booked(0L)
                                .build())
                        .isVaccinated(false)
                        .build()
        ));
        doNothing().when(vaccinationPassRepository).delete(any());
        vaccinationPassService.delete(1L);
    }

    @Test
    void findAllVaccinationPassSuccess_Test() {
        when(vaccinationPassRepository.findAll()).thenReturn(List.of(
                VaccinationPass.builder()
                        .id(1L)
                        .vaccinationSessions(VaccinationSessions.builder()
                                .booked(0L)
                                .build())
                        .isVaccinated(false)
                        .registeredBy(
                                Users.builder()
                                        .id(1L)
                                        .build()
                        )
                        .vaccine(Vaccine.builder()
                                .id(1L)
                                .build())
                        .build(),
                VaccinationPass.builder()
                        .id(2L)
                        .registeredBy(
                                Users.builder()
                                        .id(1L)
                                        .build()
                        )
                        .familyMembers(FamilyMembers.builder()
                                .id(1L)
                                .build())
                        .vaccinationSessions(VaccinationSessions.builder()
                                .booked(0L)
                                .build())
                        .vaccine(Vaccine.builder()
                                .id(1L)
                                .build())
                        .isVaccinated(false)
                        .build()
        ));

        List<VaccinationPassRequest> vaccinationPassRequests = vaccinationPassService.findAll(null);
        assertEquals(1L, vaccinationPassRequests.get(0).getId());
        assertEquals(2L, vaccinationPassRequests.get(1).getId());
    }

    @Test
    void findAllVaccinationPassByUserIdSuccess_Test() {
        when(vaccinationPassRepository.findAllByRegisteredById(any())).thenReturn(List.of(
                VaccinationPass.builder()
                        .id(1L)
                        .vaccinationSessions(VaccinationSessions.builder()
                                .booked(0L)
                                .build())
                        .isVaccinated(false)
                        .registeredBy(
                                Users.builder()
                                        .id(1L)
                                        .build()
                        )
                        .vaccine(Vaccine.builder()
                                .id(1L)
                                .build())
                        .build(),
                VaccinationPass.builder()
                        .id(2L)
                        .registeredBy(
                                Users.builder()
                                        .id(1L)
                                        .build()
                        )
                        .familyMembers(FamilyMembers.builder()
                                .id(1L)
                                .build())
                        .vaccinationSessions(VaccinationSessions.builder()
                                .booked(0L)
                                .build())
                        .vaccine(Vaccine.builder()
                                .id(1L)
                                .build())
                        .isVaccinated(false)
                        .build()
        ));

        List<VaccinationPassRequest> vaccinationPassRequests = vaccinationPassService.findAll(1L);
        assertEquals(1L, vaccinationPassRequests.get(0).getId());
        assertEquals(2L, vaccinationPassRequests.get(1).getId());
    }

    @Test
    void findVaccinationPassByIdSuccess_Test() {
        when(vaccinationPassRepository.findById(any())).thenReturn(Optional.of(
                VaccinationPass.builder()
                        .id(1L)
                        .vaccinationSessions(VaccinationSessions.builder()
                                .booked(0L)
                                .build())
                        .isVaccinated(false)
                        .registeredBy(
                                Users.builder()
                                        .id(1L)
                                        .build()
                        )
                        .vaccine(Vaccine.builder()
                                .id(1L)
                                .build())
                        .build()
        ));

        VaccinationPassRequest vaccinationPassRequest = vaccinationPassService.find(1L);
        assertEquals(1L, vaccinationPassRequest.getId());
    }

    @Test
    void findVaccinationPassByIdWithFamilyMemberSuccess_Test() {
        when(vaccinationPassRepository.findById(any())).thenReturn(Optional.of(
                VaccinationPass.builder()
                        .id(1L)
                        .vaccinationSessions(VaccinationSessions.builder()
                                .booked(0L)
                                .build())
                        .isVaccinated(false)
                        .registeredBy(
                                Users.builder()
                                        .id(1L)
                                        .build()
                        )
                        .familyMembers(FamilyMembers.builder()
                                .id(1L)
                                .build())
                        .vaccine(Vaccine.builder()
                                .id(1L)
                                .build())
                        .build()
        ));

        VaccinationPassRequest vaccinationPassRequest = vaccinationPassService.find(1L);
        assertEquals(1L, vaccinationPassRequest.getId());
    }

    @Test
    void findVaccinationPassByIdFail_Test() {
        when(vaccinationPassRepository.findById(any())).thenReturn(Optional.empty());

        try {
            VaccinationPassRequest vaccinationPassRequest = vaccinationPassService.find(1L);
            fail();
        } catch (BusinessFlowException e) {
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void updateVaccinationPassNotFoundFail_Test() {
        when(vaccinationPassRepository.findById(any())).thenReturn(Optional.empty());

        try {
            VaccinationPassRequest vaccinationPassRequest = vaccinationPassService.update(1L, VaccinationPassRequest.builder()
                    .vaccinationSession(VaccinationSessionRequest.builder()
                            .id(1L)
                            .build())
                    .registeredBy(UsersRequest.builder()
                            .id(1L)
                            .build())
                    .build());
            fail();
        } catch (BusinessFlowException e) {

        } catch (Exception e){
            fail();
        }
    }

    @Test
    void updateVaccinationPassAlreadyVaccinatedFail_Test() {
        when(vaccinationPassRepository.findById(any())).thenReturn(Optional.of(
                VaccinationPass.builder()
                        .id(1L)
                        .vaccinationSessions(VaccinationSessions.builder()
                                .booked(0L)
                                .build())
                        .isVaccinated(true)
                        .registeredBy(
                                Users.builder()
                                        .id(1L)
                                        .build()
                        )
                        .familyMembers(FamilyMembers.builder()
                                .id(1L)
                                .build())
                        .vaccine(Vaccine.builder()
                                .id(1L)
                                .build())
                        .build()
        ));

        try {
            VaccinationPassRequest vaccinationPassRequest = vaccinationPassService.update(1L, VaccinationPassRequest.builder()
                    .vaccinationSession(VaccinationSessionRequest.builder()
                            .id(1L)
                            .build())
                    .registeredBy(UsersRequest.builder()
                            .id(1L)
                            .build())
                    .build());
            fail();
        } catch (BusinessFlowException e) {

        } catch (Exception e){
            fail();
        }
    }

    @Test
    void updateVaccinationPassSuccess_Test() {
        when(vaccinationPassRepository.findById(any())).thenReturn(Optional.of(
                VaccinationPass.builder()
                        .id(1L)
                        .vaccinationSessions(VaccinationSessions.builder()
                                .booked(0L)
                                .build())
                        .isVaccinated(false)
                        .registeredBy(
                                Users.builder()
                                        .id(1L)
                                        .build()
                        )
                        .familyMembers(FamilyMembers.builder()
                                .id(1L)
                                .build())
                        .vaccine(Vaccine.builder()
                                .id(1L)
                                .build())
                        .build()
        ));

        VaccinationPassRequest vaccinationPassRequest = vaccinationPassService.update(1L, VaccinationPassRequest.builder()
                .vaccinationSession(VaccinationSessionRequest.builder()
                        .id(1L)
                        .build())
                .registeredBy(UsersRequest.builder()
                        .id(1L)
                        .build())
                .familyMember(FamilyMembersRequest.builder()
                        .id(1L)
                        .build())
                .build());
        assertEquals(1L, vaccinationPassRequest.getId());
    }

    @Test
    void updateStatusVaccinationPassFail_Test() {
        when(vaccinationPassRepository.findById(any())).thenReturn(Optional.empty());

        try {
            vaccinationPassService.updateStatusVaccinated(1L, VaccinationPassRequest.builder()
                    .isVaccinated(true)
                    .build());
            fail();
        } catch (BusinessFlowException e) {

        } catch (Exception e){
            fail();
        }
    }

    @Test
    void updateStatusVaccinationPassSuccess_Test() {
        when(vaccinationPassRepository.findById(any())).thenReturn(Optional.of(
                VaccinationPass.builder()
                        .id(1L)
                        .vaccinationSessions(VaccinationSessions.builder()
                                .booked(0L)
                                .build())
                        .isVaccinated(false)
                        .registeredBy(
                                Users.builder()
                                        .id(1L)
                                        .build()
                        )
                        .familyMembers(FamilyMembers.builder()
                                .id(1L)
                                .build())
                        .vaccine(Vaccine.builder()
                                .id(1L)
                                .build())
                        .build()
        ));

        vaccinationPassService.updateStatusVaccinated(1L, VaccinationPassRequest.builder()
                .isVaccinated(true)
                .build());
    }
}