package com.doganmehmet.app.repository;

import com.doganmehmet.app.entity.Company;
import com.doganmehmet.app.entity.User;
import com.doganmehmet.app.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IUserRepository extends JpaRepository<User, Long> {
    boolean existsByRole(Role role);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    Page<User> findByYearsOfExperienceBetween(int yearsOfExperienceAfter, int yearsOfExperienceBefore, Pageable pageable);

    Page<User> findAllByCompany(Company company, Pageable pageable);

    Page<User> findAllBySkillsOrExperienceContainsIgnoreCase(String skills, String experience, Pageable pageable);
}
