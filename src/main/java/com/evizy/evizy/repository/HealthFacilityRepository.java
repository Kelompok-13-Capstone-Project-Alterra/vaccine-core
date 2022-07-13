package com.evizy.evizy.repository;

import com.evizy.evizy.domain.dao.Admin;
import com.evizy.evizy.domain.dao.HealthFacility;
import com.evizy.evizy.domain.dao.VaccinationSessions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HealthFacilityRepository extends JpaRepository<HealthFacility, Long> {
    List<HealthFacility> findAllByCityId(Long cityId);
}
