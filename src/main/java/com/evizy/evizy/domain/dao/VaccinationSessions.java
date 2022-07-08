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
@Table(name = "vaccination_sessions")
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@SQLDelete(sql = "UPDATE vaccination_sessions SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class VaccinationSessions extends BaseDao implements Serializable {
    private static final long serialVersionUID = 5255877491777211915L;

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private HealthFacility healthFacility;

    @ManyToOne
    private Vaccine vaccine;

    @Column(name = "schedule_start", nullable = false)
    private Long scheduleStart;

    @Column(name = "schedule_end", nullable = false)
    private Long scheduleEnd;

    @Column(name = "quantity", nullable = false)
    private Long quantity;

    @Column(name = "booked", nullable = false)
    private Long booked;
}