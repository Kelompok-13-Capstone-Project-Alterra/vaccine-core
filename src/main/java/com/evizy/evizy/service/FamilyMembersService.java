package com.evizy.evizy.service;

import com.evizy.evizy.constant.ResponseMessage;
import com.evizy.evizy.domain.dao.FamilyMembers;
import com.evizy.evizy.domain.dao.Users;
import com.evizy.evizy.domain.dto.CitizenResponse;
import com.evizy.evizy.domain.dto.FamilyMembersRequest;
import com.evizy.evizy.domain.dto.UsersRequest;
import com.evizy.evizy.errors.BusinessFlowException;
import com.evizy.evizy.repository.FamilyMembersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FamilyMembersService {
    private final FamilyMembersRepository familyMembersRepository;
    private final UsersService usersService;

    public FamilyMembersRequest create(FamilyMembersRequest request) throws BusinessFlowException {
        List<CitizenResponse> citizens = usersService.getAllCitizen();

        CitizenResponse citizen = null;
        for (CitizenResponse e : citizens) {
            if (e.getNik().equalsIgnoreCase(request.getNik()))
                citizen = e;
        }

        if (citizen == null) {
            log.error("Nik not found!");
            throw new BusinessFlowException(HttpStatus.BAD_REQUEST, ResponseMessage.INVALID_NIK, "Nik not found!");
        }

        FamilyMembers familyMembers = FamilyMembers.builder()
                .name(request.getName())
                .users(Users.builder()
                        .id(request.getUser().getId())
                        .build())
                .nik(request.getNik())
                .dateOfBirth(request.getDateOfBirth())
                .gender(request.getGender())
                .relationship(request.getRelationship())
                .build();
        familyMembersRepository.save(familyMembers);
        return FamilyMembersRequest.builder()
                .id(familyMembers.getId())
                .user(UsersRequest.builder()
                        .id(request.getUser().getId())
                        .build())
                .nik(familyMembers.getNik())
                .dateOfBirth(familyMembers.getDateOfBirth())
                .gender(familyMembers.getGender())
                .relationship(familyMembers.getRelationship())
                .name(familyMembers.getName())
                .build();
    }

    public void delete(Long id) throws BusinessFlowException {
        Optional<FamilyMembers> optionalFamilyMembers = familyMembersRepository.findById(id);
        if (optionalFamilyMembers.isEmpty()) {
            throw new BusinessFlowException(HttpStatus.BAD_REQUEST, ResponseMessage.NOT_FOUND, "Family member not found!");
        }

        familyMembersRepository.delete(optionalFamilyMembers.get());
    }

    public FamilyMembersRequest update(Long id, FamilyMembersRequest request) throws BusinessFlowException {
        Optional<FamilyMembers> optionalFamilyMembers = familyMembersRepository.findById(id);
        if (optionalFamilyMembers.isEmpty()) {
            throw new BusinessFlowException(HttpStatus.BAD_REQUEST, ResponseMessage.NOT_FOUND, "Family member not found!");
        }

        List<CitizenResponse> citizens = usersService.getAllCitizen();

        CitizenResponse citizen = null;
        for (CitizenResponse e : citizens) {
            if (e.getNik().equalsIgnoreCase(request.getNik()))
                citizen = e;
        }

        if (citizen == null) {
            log.error("Nik not found!");
            throw new BusinessFlowException(HttpStatus.BAD_REQUEST, ResponseMessage.INVALID_NIK, "Nik not found!");
        }

        FamilyMembers familyMembers = optionalFamilyMembers.get();
        familyMembers.setNik(request.getNik());
        familyMembers.setName(request.getName());
        familyMembers.setDateOfBirth(request.getDateOfBirth());
        familyMembers.setGender(request.getGender());
        familyMembers.setRelationship(request.getRelationship());
        familyMembersRepository.save(familyMembers);

        Users user = familyMembers.getUsers();

        return FamilyMembersRequest.builder()
                .id(familyMembers.getId())
                .user(UsersRequest.builder()
                        .id(user.getId())
                        .name(user.getName())
                        .build())
                .nik(familyMembers.getNik())
                .dateOfBirth(familyMembers.getDateOfBirth())
                .gender(familyMembers.getGender())
                .relationship(familyMembers.getRelationship())
                .name(familyMembers.getName())
                .build();
    }

    public FamilyMembersRequest find(Long id) {
        Optional<FamilyMembers> optionalFamilyMembers = familyMembersRepository.findById(id);
        if (optionalFamilyMembers.isEmpty())
            throw new BusinessFlowException(HttpStatus.BAD_REQUEST, ResponseMessage.BAD_REQUEST, "Family member not found!");
        FamilyMembers familyMembers = optionalFamilyMembers.get();
        Users user = familyMembers.getUsers();

        return FamilyMembersRequest.builder()
                .id(familyMembers.getId())
                .user(UsersRequest.builder()
                        .id(user.getId())
                        .name(user.getName())
                        .build())
                .nik(familyMembers.getNik())
                .dateOfBirth(familyMembers.getDateOfBirth())
                .gender(familyMembers.getGender())
                .relationship(familyMembers.getRelationship())
                .name(familyMembers.getName())
                .build();
    }

    public List<FamilyMembersRequest> findAll(Long userId) {
        List<FamilyMembers> familyMembersList;
        if (userId == null)
            familyMembersList = familyMembersRepository.findAll();
        else
            familyMembersList = familyMembersRepository.findAllByUsersId(userId);

        List<FamilyMembersRequest> familyMembersRequests = new ArrayList<>();
        for(FamilyMembers familyMembers : familyMembersList) {
            Users user = familyMembers.getUsers();

            familyMembersRequests.add(FamilyMembersRequest.builder()
                    .id(familyMembers.getId())
                    .user(UsersRequest.builder()
                            .id(user.getId())
                            .name(user.getName())
                            .build())
                    .nik(familyMembers.getNik())
                    .dateOfBirth(familyMembers.getDateOfBirth())
                    .gender(familyMembers.getGender())
                    .relationship(familyMembers.getRelationship())
                    .name(familyMembers.getName())
                    .build());
        }
        return familyMembersRequests;
    }
}

