package com.evizy.evizy.repository;

import com.evizy.evizy.domain.dao.VaccinationPass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VaccinationPassRepository extends JpaRepository<VaccinationPass, Long> {
    List<VaccinationPass> findAllByRegisteredById(Long id);
}
