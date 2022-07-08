package com.evizy.evizy.repository;

import com.evizy.evizy.domain.dao.VaccineDistribution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VaccineDistributionRepository extends JpaRepository<VaccineDistribution, Long> {
}
