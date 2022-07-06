package com.evizy.evizy.domain.dao;

import com.evizy.evizy.domain.common.BaseDao;
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
@Table(name = "health_facilities")
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE health_facilities SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class HealthFacility extends BaseDao implements Serializable {
    private static final long serialVersionUID = -1578963472758237089L;

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne
    private Admin admin;

    @ManyToOne
    private City city;
}
