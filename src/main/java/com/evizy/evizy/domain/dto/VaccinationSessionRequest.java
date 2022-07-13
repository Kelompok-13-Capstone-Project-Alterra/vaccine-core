package com.evizy.evizy.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VaccinationSessionRequest implements Serializable {
    private static final long serialVersionUID = 896144549202810066L;

    private Long id;
    private HealthFacilityRequest healthFacility;
    private VaccineRequest vaccine;

    private LocalDate scheduleDate;
    private String scheduleTimeStart;
    private String scheduleTimeEnd;
    private Long quantity;
    private Long booked;
}