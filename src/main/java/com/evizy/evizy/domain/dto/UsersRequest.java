package com.evizy.evizy.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
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
public class UsersRequest implements Serializable {
    private static final long serialVersionUID = -5852095789648986257L;

    private Long id;

    @Pattern(regexp = "^[0-9]{16}$")
    private String nik;

    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9]).{8,}$")
    private String password;

    @Pattern(regexp = "^08[0-9]{9,11}$")
    private String phoneNumber;

    @Pattern(regexp = "^[a-zA-Z]+[a-zA-Z\\s]*[a-zA-Z]+$")
    private String name;
    private Character gender;
    private LocalDate dateOfBirth;
}