package com.doganmehmet.app.repository;

import com.doganmehmet.app.entity.JobApplication;
import com.doganmehmet.app.entity.JobPosting;
import com.doganmehmet.app.entity.User;
import com.doganmehmet.app.role.ApplicationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IJobApplicationRepository extends JpaRepository<JobApplication, Long> {
    Page<JobApplication> findAllByUser(User user, Pageable pageable);

    Page<JobApplication> findAllByJobPosting(JobPosting jobPosting, Pageable pageable);

    Page<JobApplication> findAllByStatus(ApplicationStatus status, Pageable pageable);
}
