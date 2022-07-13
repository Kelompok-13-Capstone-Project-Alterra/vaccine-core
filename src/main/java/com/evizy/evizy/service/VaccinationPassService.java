package com.evizy.evizy.service;

import com.evizy.evizy.constant.ResponseMessage;
import com.evizy.evizy.domain.dao.*;
import com.evizy.evizy.domain.dto.*;
import com.evizy.evizy.errors.BusinessFlowException;
import com.evizy.evizy.repository.FamilyMembersRepository;
import com.evizy.evizy.repository.UsersRepository;
import com.evizy.evizy.repository.VaccinationPassRepository;
import com.evizy.evizy.repository.VaccinationSessionsRepository;
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
public class VaccinationPassService {
    private final VaccinationPassRepository vaccinationPassRepository;
    private final VaccinationSessionsRepository vaccinationSessionsRepository;
    private final FamilyMembersRepository familyMembersRepository;
    private final UsersRepository usersRepository;

    public VaccinationPassRequest create(VaccinationPassRequest request) throws BusinessFlowException {
        Optional<VaccinationSessions> optionalVaccinationSessions = vaccinationSessionsRepository.findById(request.getVaccinationSession().getId());

        if (optionalVaccinationSessions.isEmpty()) {
            throw new BusinessFlowException(HttpStatus.BAD_REQUEST, ResponseMessage.BAD_REQUEST, "Vaccination session not found!");
        }

        if (optionalVaccinationSessions.get().getBooked() >= optionalVaccinationSessions.get().getQuantity()) {
            throw new BusinessFlowException(HttpStatus.BAD_REQUEST, ResponseMessage.STOCK_EMPTY, "Stock empty!");
        }

        FamilyMembers familyMember = null;

        if (request.getFamilyMember() != null && request.getFamilyMember().getId() != null) {
            Optional<FamilyMembers> optionalFamilyMembers = familyMembersRepository.findById(request.getFamilyMember().getId());

            if (optionalFamilyMembers.isEmpty() || !optionalFamilyMembers.get().getUsers().getId().equals(request.getRegisteredBy().getId())) {
                throw new BusinessFlowException(HttpStatus.BAD_REQUEST, ResponseMessage.BAD_REQUEST, "Family member not found or not the part of the auth user!");
            }
            familyMember = optionalFamilyMembers.get();
        }
        VaccinationSessions vaccinationSessions = optionalVaccinationSessions.get();

        VaccinationPass vaccinationPass = VaccinationPass.builder()
                .vaccinationSessions(vaccinationSessions)
                .vaccine(vaccinationSessions.getVaccine())
                .registeredBy(Users.builder()
                        .id(request.getRegisteredBy().getId())
                        .build())
                .familyMembers(familyMember)
                .isVaccinated(false)
                .isPregnant(request.getIsPregnant())
                .medicalHistory(request.getMedicalHistory())
                .ageCategory(request.getAgeCategory())
                .idAddress(request.getIdAddress())
                .idUrbanVillage(request.getIdUrbanVillage())
                .idSubDistrict(request.getIdSubDistrict())
                .idCity(request.getIdCity())
                .idProvince(request.getIdProvince())
                .currAddress(request.getCurrAddress())
                .currUrbanVillage(request.getCurrUrbanVillage())
                .currSubDistrict(request.getCurrSubDistrict())
                .currCity(request.getCurrCity())
                .currProvince(request.getCurrProvince())
                .build();

        if (familyMember != null) {
            vaccinationPass.setNik(familyMember.getNik());
            vaccinationPass.setName(familyMember.getName());
            vaccinationPass.setDateOfBirth(familyMember.getDateOfBirth());
            vaccinationPass.setPhoneNumber(familyMember.getPhoneNumber());
            vaccinationPass.setGender(familyMember.getGender());
        } else {
            Optional<Users> optionalUsers = usersRepository.findById(request.getRegisteredBy().getId());
            Users users = optionalUsers.get();

            vaccinationPass.setNik(users.getNik());
            vaccinationPass.setName(users.getName());
            vaccinationPass.setDateOfBirth(users.getDateOfBirth());
            vaccinationPass.setPhoneNumber(users.getPhoneNumber());
            vaccinationPass.setGender(users.getGender());
        }

        vaccinationSessions.setBooked(vaccinationSessions.getBooked() + 1);
        vaccinationSessionsRepository.save(vaccinationSessions);
        vaccinationPassRepository.save(vaccinationPass);

        VaccinationPassRequest response = VaccinationPassRequest.builder()
                .id(vaccinationPass.getId())
                .vaccinationSession(VaccinationSessionRequest.builder()
                        .id(vaccinationSessions.getId())
                        .build())
                .vaccine(VaccineRequest.builder()
                        .id(vaccinationSessions.getVaccine().getId())
                        .build())
                .registeredBy(UsersRequest.builder()
                        .id(request.getRegisteredBy().getId())
                        .build())
                .nik(vaccinationPass.getNik())
                .name(vaccinationPass.getName())
                .dateOfBirth(vaccinationPass.getDateOfBirth())
                .phoneNumber(vaccinationPass.getPhoneNumber())
                .gender(vaccinationPass.getGender())
                .isPregnant(vaccinationPass.getIsPregnant())
                .medicalHistory(vaccinationPass.getMedicalHistory())
                .ageCategory(vaccinationPass.getAgeCategory())
                .idAddress(vaccinationPass.getIdAddress())
                .idUrbanVillage(vaccinationPass.getIdUrbanVillage())
                .idSubDistrict(vaccinationPass.getIdSubDistrict())
                .idCity(vaccinationPass.getIdCity())
                .idProvince(vaccinationPass.getIdProvince())
                .currAddress(vaccinationPass.getCurrAddress())
                .currUrbanVillage(vaccinationPass.getCurrUrbanVillage())
                .currSubDistrict(vaccinationPass.getCurrSubDistrict())
                .currCity(vaccinationPass.getCurrCity())
                .currProvince(vaccinationPass.getCurrProvince())
                .build();

        if (familyMember != null) {
            response.setFamilyMember(FamilyMembersRequest.builder()
                    .id(familyMember.getId())
                    .build());
        }

        return response;
    }

    public void delete(Long id) throws BusinessFlowException {
        Optional<VaccinationPass> optionalVaccinationPass = vaccinationPassRepository.findById(id);

        if (optionalVaccinationPass.isEmpty()) {
            throw new BusinessFlowException(HttpStatus.BAD_REQUEST, ResponseMessage.NOT_FOUND, "Vaccination pass not found!");
        }

        VaccinationPass vaccinationPass = optionalVaccinationPass.get();

        if (vaccinationPass.getIsVaccinated()) {
            throw new BusinessFlowException(HttpStatus.BAD_REQUEST, ResponseMessage.CITIZEN_ALREADY_VACCINATED, "Can't update the ticket, citizen already vaccinated!");
        }

        VaccinationSessions vaccinationSessions = vaccinationPass.getVaccinationSessions();
        vaccinationSessions.setBooked(vaccinationSessions.getBooked() - 1);
        vaccinationPassRepository.delete(vaccinationPass);
    }

    public void updateStatusVaccinated(Long id, VaccinationPassRequest request) throws BusinessFlowException {
        Optional<VaccinationPass> optionalVaccinationPass = vaccinationPassRepository.findById(id);

        if (optionalVaccinationPass.isEmpty()) {
            throw new BusinessFlowException(HttpStatus.BAD_REQUEST, ResponseMessage.NOT_FOUND, "Vaccination pass not found!");
        }

        VaccinationPass vaccinationPass = optionalVaccinationPass.get();
        vaccinationPass.setIsVaccinated(request.getIsVaccinated());
        vaccinationPassRepository.save(vaccinationPass);
    }

    public VaccinationPassRequest update(Long id, VaccinationPassRequest request) throws BusinessFlowException {
        Optional<VaccinationPass> optionalVaccinationPass = vaccinationPassRepository.findById(id);

        if (optionalVaccinationPass.isEmpty()) {
            throw new BusinessFlowException(HttpStatus.BAD_REQUEST, ResponseMessage.NOT_FOUND, "Vaccination pass not found!");
        }

        VaccinationPass vaccinationPass = optionalVaccinationPass.get();

        if (vaccinationPass.getIsVaccinated()) {
            throw new BusinessFlowException(HttpStatus.BAD_REQUEST, ResponseMessage.CITIZEN_ALREADY_VACCINATED, "Can't update the ticket, citizen already vaccinated!");
        }

        vaccinationPass.setIsPregnant(request.getIsPregnant());
        vaccinationPass.setMedicalHistory(request.getMedicalHistory());
        vaccinationPass.setAgeCategory(request.getAgeCategory());
        vaccinationPass.setIdAddress(request.getIdAddress());
        vaccinationPass.setIdUrbanVillage(request.getIdUrbanVillage());
        vaccinationPass.setIdSubDistrict(request.getIdSubDistrict());
        vaccinationPass.setIdCity(request.getIdCity());
        vaccinationPass.setIdProvince(request.getIdProvince());
        vaccinationPass.setCurrAddress(request.getCurrAddress());
        vaccinationPass.setCurrUrbanVillage(request.getCurrUrbanVillage());
        vaccinationPass.setCurrSubDistrict(request.getCurrSubDistrict());
        vaccinationPass.setCurrCity(request.getCurrCity());
        vaccinationPass.setCurrProvince(request.getCurrProvince());
        vaccinationPassRepository.save(vaccinationPass);

        VaccinationPassRequest response = VaccinationPassRequest.builder()
                .id(vaccinationPass.getId())
                .vaccinationSession(VaccinationSessionRequest.builder()
                        .id(vaccinationPass.getVaccinationSessions().getId())
                        .build())
                .vaccine(VaccineRequest.builder()
                        .id(vaccinationPass.getVaccine().getId())
                        .build())
                .registeredBy(UsersRequest.builder()
                        .id(vaccinationPass.getRegisteredBy().getId())
                        .build())
                .nik(vaccinationPass.getNik())
                .name(vaccinationPass.getName())
                .dateOfBirth(vaccinationPass.getDateOfBirth())
                .phoneNumber(vaccinationPass.getPhoneNumber())
                .gender(vaccinationPass.getGender())
                .ageCategory(vaccinationPass.getAgeCategory())
                .isVaccinated(vaccinationPass.getIsVaccinated())
                .isPregnant(vaccinationPass.getIsPregnant())
                .medicalHistory(vaccinationPass.getMedicalHistory())
                .idAddress(vaccinationPass.getIdAddress())
                .idUrbanVillage(vaccinationPass.getIdUrbanVillage())
                .idSubDistrict(vaccinationPass.getIdSubDistrict())
                .idCity(vaccinationPass.getIdCity())
                .idProvince(vaccinationPass.getIdProvince())
                .currAddress(vaccinationPass.getCurrAddress())
                .currUrbanVillage(vaccinationPass.getCurrUrbanVillage())
                .currSubDistrict(vaccinationPass.getCurrSubDistrict())
                .currCity(vaccinationPass.getCurrCity())
                .currProvince(vaccinationPass.getCurrProvince())
                .build();

        if (vaccinationPass.getFamilyMembers() != null) {
            response.setFamilyMember(FamilyMembersRequest.builder()
                    .id(vaccinationPass.getFamilyMembers().getId())
                    .build());
        }

        return response;
    }

    public VaccinationPassRequest find(Long id) {
        Optional<VaccinationPass> optionalVaccinationPass = vaccinationPassRepository.findById(id);
        if (optionalVaccinationPass.isEmpty())
            throw new BusinessFlowException(HttpStatus.BAD_REQUEST, ResponseMessage.BAD_REQUEST, "Vaccination pass not found!");
        VaccinationPass vaccinationPass = optionalVaccinationPass.get();
        VaccinationSessions vaccinationSessions = vaccinationPass.getVaccinationSessions();
        FamilyMembers familyMember = vaccinationPass.getFamilyMembers();
        Users registeredBy = vaccinationPass.getRegisteredBy();
        Vaccine vaccine = vaccinationPass.getVaccine();
        VaccinationPassRequest response = VaccinationPassRequest.builder()
                .id(vaccinationPass.getId())
                .vaccinationSession(VaccinationSessionRequest.builder()
                        .id(vaccinationSessions.getId())
                        .build())
                .vaccine(VaccineRequest.builder()
                        .id(vaccine.getId())
                        .name(vaccine.getName())
                        .build())
                .registeredBy(UsersRequest.builder()
                        .id(registeredBy.getId())
                        .name(registeredBy.getName())
                        .build())
                .nik(vaccinationPass.getNik())
                .name(vaccinationPass.getName())
                .dateOfBirth(vaccinationPass.getDateOfBirth())
                .phoneNumber(vaccinationPass.getPhoneNumber())
                .gender(vaccinationPass.getGender())
                .ageCategory(vaccinationPass.getAgeCategory())
                .isVaccinated(vaccinationPass.getIsVaccinated())
                .isPregnant(vaccinationPass.getIsPregnant())
                .medicalHistory(vaccinationPass.getMedicalHistory())
                .idAddress(vaccinationPass.getIdAddress())
                .idUrbanVillage(vaccinationPass.getIdUrbanVillage())
                .idSubDistrict(vaccinationPass.getIdSubDistrict())
                .idCity(vaccinationPass.getIdCity())
                .idProvince(vaccinationPass.getIdProvince())
                .currAddress(vaccinationPass.getCurrAddress())
                .currUrbanVillage(vaccinationPass.getCurrUrbanVillage())
                .currSubDistrict(vaccinationPass.getCurrSubDistrict())
                .currCity(vaccinationPass.getCurrCity())
                .currProvince(vaccinationPass.getCurrProvince())
                .build();

        if (familyMember != null) {
            response.setFamilyMember(FamilyMembersRequest.builder()
                    .id(familyMember.getId())
                    .name(familyMember.getName())
                    .build());
        }

        return response;
    }

    public List<VaccinationPassRequest> findAll(Long userId) {
        List<VaccinationPass> vaccinationPassList;
        if (userId == null)
            vaccinationPassList = vaccinationPassRepository.findAll();
        else
            vaccinationPassList = vaccinationPassRepository.findAllByRegisteredById(userId);

        List<VaccinationPassRequest> vaccinationPassRequests = new ArrayList<>();
        for(VaccinationPass vaccinationPass : vaccinationPassList) {
            VaccinationSessions vaccinationSessions = vaccinationPass.getVaccinationSessions();
            FamilyMembers familyMember = vaccinationPass.getFamilyMembers();
            Users registeredBy = vaccinationPass.getRegisteredBy();
            Vaccine vaccine = vaccinationPass.getVaccine();

            VaccinationPassRequest vaccinationPassRequest = VaccinationPassRequest.builder()
                    .id(vaccinationPass.getId())
                    .vaccinationSession(VaccinationSessionRequest.builder()
                            .id(vaccinationSessions.getId())
                            .build())
                    .vaccine(VaccineRequest.builder()
                            .id(vaccine.getId())
                            .name(vaccine.getName())
                            .build())
                    .registeredBy(UsersRequest.builder()
                            .id(registeredBy.getId())
                            .name(registeredBy.getName())
                            .build())
                    .nik(vaccinationPass.getNik())
                    .name(vaccinationPass.getName())
                    .dateOfBirth(vaccinationPass.getDateOfBirth())
                    .phoneNumber(vaccinationPass.getPhoneNumber())
                    .gender(vaccinationPass.getGender())
                    .ageCategory(vaccinationPass.getAgeCategory())
                    .isVaccinated(vaccinationPass.getIsVaccinated())
                    .isPregnant(vaccinationPass.getIsPregnant())
                    .medicalHistory(vaccinationPass.getMedicalHistory())
                    .idAddress(vaccinationPass.getIdAddress())
                    .idUrbanVillage(vaccinationPass.getIdUrbanVillage())
                    .idSubDistrict(vaccinationPass.getIdSubDistrict())
                    .idCity(vaccinationPass.getIdCity())
                    .idProvince(vaccinationPass.getIdProvince())
                    .currAddress(vaccinationPass.getCurrAddress())
                    .currUrbanVillage(vaccinationPass.getCurrUrbanVillage())
                    .currSubDistrict(vaccinationPass.getCurrSubDistrict())
                    .currCity(vaccinationPass.getCurrCity())
                    .currProvince(vaccinationPass.getCurrProvince())
                    .build();

            if (familyMember != null) {
                vaccinationPassRequest.setFamilyMember(FamilyMembersRequest.builder()
                        .id(familyMember.getId())
                        .name(familyMember.getName())
                        .build());
            }

            vaccinationPassRequests.add(vaccinationPassRequest);
        }
        return vaccinationPassRequests;
    }
}
