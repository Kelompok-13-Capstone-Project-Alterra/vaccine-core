package com.evizy.evizy.repository;

import com.evizy.evizy.domain.dao.Admin;
import com.evizy.evizy.domain.dao.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
    Admin getDistinctTopByUsername(String username);
    Admin findByUsername(String username);
}
