package com.evizy.evizy.repository;

import com.evizy.evizy.domain.dao.HealthFacilitiesVaccines;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HealthFacilitiesVaccinesRepository extends JpaRepository<HealthFacilitiesVaccines, HealthFacilitiesVaccines.HealthFacilitiesVaccinesId> {
    public List<HealthFacilitiesVaccines> findAllByHealthFacilityId(Long id);
}
