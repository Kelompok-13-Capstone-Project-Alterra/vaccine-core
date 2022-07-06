package com.evizy.evizy.repository;

import com.evizy.evizy.domain.dao.Admin;
import com.evizy.evizy.domain.dao.HealthFacility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HealthFacilityRepository extends JpaRepository<HealthFacility, Long> {
}
