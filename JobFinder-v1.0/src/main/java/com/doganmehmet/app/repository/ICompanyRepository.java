package com.doganmehmet.app.repository;

import com.doganmehmet.app.entity.Company;
import com.doganmehmet.app.entity.JobPosting;
import com.doganmehmet.app.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ICompanyRepository extends JpaRepository<Company, Long> {

    Optional<Company> findByNameOrEmailOrPhone(String name, String email, String phone);

    @Query("select u from User u where u.company =:company")
    Page<User> findUsersByCompany(Company company, Pageable pageable);

    @Query("select jp from JobPosting jp where jp.company =:company")
    Page<JobPosting> findJobPostingsByCompany(Company company, Pageable pageable);

    Page<Company> findByCompanyId(long companyId, Pageable pageable);

    Optional<Company> findByName(String name);
}
