package com.evizy.evizy.domain.dao;

import com.evizy.evizy.domain.common.BaseDao;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@Entity
@Table(name = "vaccination_pass")
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@SQLDelete(sql = "UPDATE vaccination_pass SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class VaccinationPass extends BaseDao implements Serializable {
    private static final long serialVersionUID = -7536149361832240256L;

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Users registeredBy;

    @ManyToOne
    private VaccinationSessions vaccinationSessions;

    @ManyToOne
    private FamilyMembers familyMembers;

    @ManyToOne
    private Vaccine vaccine;

    @Column(name = "nik", nullable = false)
    private String nik;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Column(name = "gender", nullable = false)
    private char gender;

    @Column(name = "landline_phone")
    private String landlinePhone;

    @Column(name = "email")
    private String email;

    @Column(name = "is_vaccinated", nullable = false)
    private Boolean isVaccinated;

    @Column(name = "medical_history")
    private String medicalHistory;

    @Column(name = "age_category")
    private String ageCategory;

    @Column(name = "is_pregnant", nullable = false)
    private Boolean isPregnant;

    @Column(name = "id_address")
    private String idAddress;

    @Column(name = "id_urban_village")
    private String idUrbanVillage;

    @Column(name = "id_sub_district")
    private String idSubDistrict;

    @Column(name = "id_city")
    private String idCity;

    @Column(name = "id_province")
    private String idProvince;

    @Column(name = "curr_address")
    private String currAddress;

    @Column(name = "curr_urban_village")
    private String currUrbanVillage;

    @Column(name = "curr_sub_district")
    private String currSubDistrict;

    @Column(name = "curr_city")
    private String currCity;

    @Column(name = "curr_province")
    private String currProvince;
}
