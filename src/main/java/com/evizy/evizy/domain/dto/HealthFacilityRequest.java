package com.evizy.evizy.domain.dto;

import com.evizy.evizy.domain.dao.Admin;
import com.evizy.evizy.domain.dao.City;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HealthFacilityRequest implements Serializable {
    private static final long serialVersionUID = -8382189692562014825L;

    private Long id;
    @Pattern(regexp = "^[a-zA-Z]+[a-zA-Z\\s\\.0-9]*[a-zA-Z0-9]+$")
    private String name;

    private AdminsRequest admin;
    private CityRequest city;

}