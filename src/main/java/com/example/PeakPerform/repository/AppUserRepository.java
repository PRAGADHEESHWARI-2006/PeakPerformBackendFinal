
package com.example.PeakPerform.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import com.example.PeakPerform.entity.AppUserEntity;
import com.example.PeakPerform.enums.Role;

@Repository
public interface AppUserRepository extends JpaRepository<AppUserEntity, Long> {

    Optional<AppUserEntity> findByEmail(String email);

    boolean existsByEmail(String email);

    List<AppUserEntity> findByRole(Role role);

    List<AppUserEntity> findByDepartmentAndIsActiveTrue(String department);

    @Query("SELECT u FROM AppUserEntity u WHERE u.isActive = true ORDER BY u.fullName ASC")
List<AppUserEntity> findAllActiveUsers();

   @Query("SELECT COUNT(u) FROM AppUserEntity u WHERE u.role = :role AND u.isActive = true")
long countActiveByRole(@Param("role") Role role);
}