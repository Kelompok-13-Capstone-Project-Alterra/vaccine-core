package com.evizy.evizy.repository;

import com.evizy.evizy.domain.dao.FamilyMembers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FamilyMembersRepository extends JpaRepository<FamilyMembers, Long> {
    List<FamilyMembers> findAllByUsersId(Long userId);
}
