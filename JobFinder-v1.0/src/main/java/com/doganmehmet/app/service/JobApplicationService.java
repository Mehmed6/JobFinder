package com.doganmehmet.app.service;

import com.doganmehmet.app.dto.jobapplication.JobApplicationRequest;
import com.doganmehmet.app.entity.JobApplication;
import com.doganmehmet.app.exception.ApiException;
import com.doganmehmet.app.exception.MyError;
import com.doganmehmet.app.repository.IJobApplicationRepository;
import com.doganmehmet.app.repository.IJobPostingRepository;
import com.doganmehmet.app.repository.IUserRepository;
import com.doganmehmet.app.role.ApplicationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class JobApplicationService {

    private final IJobApplicationRepository m_jobApplicationRepository;
    private final IUserRepository m_userRepository;
    private final IJobPostingRepository m_jobPostingRepository;

    public JobApplicationService(IJobApplicationRepository jobApplicationRepository, IUserRepository userRepository, IJobPostingRepository jobPostingRepository)
    {
        m_jobApplicationRepository = jobApplicationRepository;
        m_userRepository = userRepository;
        m_jobPostingRepository = jobPostingRepository;
    }

    private ApplicationStatus checkAndGetStatus(String status)
    {
        try {
            return ApplicationStatus.valueOf(status);
        }
        catch (IllegalArgumentException ignored) {
            throw new ApiException(MyError.INVALID_STATUS);
        }
    }

    public void save(JobApplicationRequest jobApplicationRequest)
    {
        var user = m_userRepository.findById(jobApplicationRequest.getUserId())
                .orElseThrow(() -> new ApiException(MyError.USER_NOT_FOUND));

        var jobPosting = m_jobPostingRepository.findById(jobApplicationRequest.getJobPostingId())
                .orElseThrow(() -> new ApiException(MyError.JOB_NOT_FOUND));

        m_jobApplicationRepository.save(new JobApplication(user, jobPosting));
    }

    public Page<JobApplication> findJobApplicationsByUser(Long userId, int page, int size)
    {
        var pageable = PageRequest.of(page, size);
        var user = m_userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(MyError.USER_NOT_FOUND));

        return m_jobApplicationRepository.findAllByUser(user, pageable);
    }

    public Page<JobApplication> findJobApplicationsByJobPosting(Long jobPostingId, int page, int size)
    {
        var pageable = PageRequest.of(page, size);
        var jobPosting = m_jobPostingRepository.findById(jobPostingId)
                .orElseThrow(() -> new ApiException(MyError.USER_NOT_FOUND));

        return m_jobApplicationRepository.findAllByJobPosting(jobPosting, pageable);
    }

    public Page<JobApplication> findJobApplicationsByStatus(String statusDTO, int page, int size)
    {
        var pageable = PageRequest.of(page, size);
        var status = checkAndGetStatus(statusDTO.toUpperCase());

        return m_jobApplicationRepository.findAllByStatus(status, pageable);
    }
}
