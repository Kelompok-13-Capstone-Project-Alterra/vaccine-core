package com.evizy.evizy.domain.dao;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Builder
@Entity
@Table(name = "health_facilities_vaccines")
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@IdClass(HealthFacilitiesVaccines.HealthFacilitiesVaccinesId.class)
public class HealthFacilitiesVaccines implements Serializable {
    private static final long serialVersionUID = -4061492348680598133L;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class HealthFacilitiesVaccinesId implements Serializable {
        private static final long serialVersionUID = 5574853626264009169L;

        private Long healthFacility;
        private Long vaccine;
    }

    @Id
    @ManyToOne
    @JoinColumn(name = "health_facility_id", nullable = false)
    private HealthFacility healthFacility;

    @Id
    @ManyToOne
    @JoinColumn(name = "vaccine_id", nullable = false)
    private Vaccine vaccine;

    @Column(name = "stock", nullable = false)
    private Long stock;
}
