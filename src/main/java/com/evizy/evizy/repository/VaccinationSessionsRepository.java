package com.evizy.evizy.repository;

import com.evizy.evizy.domain.dao.VaccinationSessions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VaccinationSessionsRepository extends JpaRepository<VaccinationSessions, Long> {
}
