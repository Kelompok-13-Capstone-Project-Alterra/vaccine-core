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
import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@Entity
@Table(name = "family_members")
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE family_members SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class FamilyMembers extends BaseDao implements Serializable {
    private static final long serialVersionUID = 1477169760751883177L;

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Users users;

    @Column(name = "nik", nullable = false)
    private String nik;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "gender")
    private Character gender;

    @Column(name = "relationship")
    private String relationship;
}
