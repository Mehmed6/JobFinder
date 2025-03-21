package com.doganmehmet.app.repository;

import com.doganmehmet.app.entity.Company;
import com.doganmehmet.app.entity.JobPosting;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface IJobPostingRepository extends JpaRepository<JobPosting, Long> {

    Page<JobPosting> findAllByCompany(Company company, Pageable pageable);

    Page<JobPosting> findAllByTitleOrDescriptionOrRequiredSkillsContainsIgnoreCase(String title, String description, String requiredSkills, Pageable pageable);

    Page<JobPosting> findAllByMinExperience(int minExperience, Pageable pageable);

    @Query("SELECT j FROM JobPosting j WHERE j.isActive = :isActive")
    Page<JobPosting> findAllByActive(@Param("isActive") boolean isActive, Pageable pageable);

    Page<JobPosting> findAllByLocation(String location, Pageable pageable);

    Page<JobPosting> findAllByMinExperienceGreaterThanEqual(int minExperienceIsGreaterThan, Pageable pageable);
}
