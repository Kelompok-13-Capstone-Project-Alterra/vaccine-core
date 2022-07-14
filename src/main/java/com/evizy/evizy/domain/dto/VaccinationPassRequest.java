package com.evizy.evizy.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Pattern;
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
    @Pattern(regexp = "^[a-zA-Z]+[a-zA-Z\\s]*[a-zA-Z]+$")
    private String ageCategory;

    private Boolean isVaccinated;

    private String medicalHistory;
    private Boolean isPregnant;

    @Pattern(regexp = "^[a-zA-Z]+[a-zA-Z\\s\\.0-9]*[a-zA-Z0-9]+$")
    private String idAddress;
    @Pattern(regexp = "^[a-zA-Z]+[a-zA-Z\\s]*[a-zA-Z]+$")
    private String idUrbanVillage;
    @Pattern(regexp = "^[a-zA-Z]+[a-zA-Z\\s]*[a-zA-Z]+$")
    private String idSubDistrict;
    @Pattern(regexp = "^[a-zA-Z]+[a-zA-Z\\s]*[a-zA-Z]+$")
    private String idCity;
    @Pattern(regexp = "^[a-zA-Z]+[a-zA-Z\\s]*[a-zA-Z]+$")
    private String idProvince;

    @Pattern(regexp = "^[a-zA-Z]+[a-zA-Z\\s\\.0-9]*[a-zA-Z0-9]+$")
    private String currAddress;
    @Pattern(regexp = "^[a-zA-Z]+[a-zA-Z\\s]*[a-zA-Z]+$")
    private String currUrbanVillage;
    @Pattern(regexp = "^[a-zA-Z]+[a-zA-Z\\s]*[a-zA-Z]+$")
    private String currSubDistrict;
    @Pattern(regexp = "^[a-zA-Z]+[a-zA-Z\\s]*[a-zA-Z]+$")
    private String currCity;
    @Pattern(regexp = "^[a-zA-Z]+[a-zA-Z\\s]*[a-zA-Z]+$")
    private String currProvince;
}
