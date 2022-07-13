package com.evizy.evizy.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VaccinationPassRequest implements Serializable {
    private static final long serialVersionUID = 9049216775949026947L;

    private Long id;

    private UsersRequest registeredBy;
    private VaccinationSessionRequest vaccinationSession;
    private FamilyMembersRequest familyMember;
    private VaccineRequest vaccine;

    private String nik;
    private String name;
    private LocalDate dateOfBirth;
    private String phoneNumber;
    private char gender;
    private String landlinePhone;
    private String email;
    private String ageCategory;

    private Boolean isVaccinated;

    private String medicalHistory;
    private Boolean isPregnant;

    private String idAddress;
    private String idUrbanVillage;
    private String idSubDistrict;
    private String idCity;
    private String idProvince;

    private String currAddress;
    private String currUrbanVillage;
    private String currSubDistrict;
    private String currCity;
    private String currProvince;
}
