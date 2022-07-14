package com.evizy.evizy.repository;

import com.evizy.evizy.domain.dao.Vaccine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface VaccineRepository extends JpaRepository<Vaccine, Long> {
    @Query(value = "SELECT COUNT(*) FROM vaccines WHERE lower(name) = :name",nativeQuery = true)
    Long countAllVaccinesByLowerName(@Param("name") String name);
}

