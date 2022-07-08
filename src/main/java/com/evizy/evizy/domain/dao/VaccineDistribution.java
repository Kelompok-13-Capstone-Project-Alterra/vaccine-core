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

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@Entity
@Table(name = "vaccine_distribution")
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@SQLDelete(sql = "UPDATE vaccine_distribution SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class VaccineDistribution extends BaseDao implements Serializable {
    private static final long serialVersionUID = 8339175060672117153L;

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private HealthFacility healthFacility;

    @ManyToOne
    private Vaccine vaccine;

    @ManyToOne
    private VaccinationSessions vaccinationSession;

    @Column(name = "quantity", nullable = false)
    private Long quantity;
}
