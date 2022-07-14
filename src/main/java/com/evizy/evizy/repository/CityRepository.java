package com.evizy.evizy.repository;

import com.evizy.evizy.domain.dao.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CityRepository extends JpaRepository<City, Long> {
    @Query(value = "SELECT COUNT(*) FROM cities WHERE lower(name) = :name",nativeQuery = true)
    Long countAllCitiesByLowerName(@Param("name") String name);
}
