package com.evizy.evizy.repository;

import com.evizy.evizy.domain.dao.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersRepository extends JpaRepository<Users, Long> {
    Users getDistinctTopByNik(String nik);
}
