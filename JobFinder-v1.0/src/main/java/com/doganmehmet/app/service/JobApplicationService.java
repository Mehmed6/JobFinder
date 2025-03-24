package com.doganmehmet.app.service;

import com.doganmehmet.app.dto.jobapplication.JobApplicationRequest;
import com.doganmehmet.app.entity.JobApplication;
import com.doganmehmet.app.entity.User;
import com.doganmehmet.app.enums.LogType;
import com.doganmehmet.app.exception.ApiException;
import com.doganmehmet.app.exception.MyError;
import com.doganmehmet.app.repository.IJobApplicationRepository;
import com.doganmehmet.app.repository.IJobPostingRepository;
import com.doganmehmet.app.repository.IUserRepository;
import com.doganmehmet.app.enums.ApplicationStatus;
import com.doganmehmet.app.utility.LogUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Arrays;

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
        return Arrays.stream(ApplicationStatus.values())
                .filter(appStatus -> appStatus.name().equalsIgnoreCase(status))
                .findFirst()
                .orElseThrow(() -> new ApiException(MyError.INVALID_STATUS));
    }

    private boolean checkUserAlreadyApplied(User user, long jobPostingId)
    {
        return user.getJobApplications().stream().anyMatch(jobApplication ->
                jobApplication.getJobPosting().getJobPostingId() == jobPostingId);
    }

    public void save(JobApplicationRequest jobApplicationRequest, String email)
    {
        var user = m_userRepository.findById(jobApplicationRequest.getUserId())
                .orElseThrow(() -> new ApiException(MyError.USER_NOT_FOUND));

        if (!user.getEmail().equals(email))
            throw new ApiException(MyError.USER_CANNOT_APPLY_FOR_ANOTHER_USER);

        var jobPosting = m_jobPostingRepository.findById(jobApplicationRequest.getJobPostingId())
                .orElseThrow(() -> new ApiException(MyError.JOB_NOT_FOUND));

        if (checkUserAlreadyApplied(user, jobApplicationRequest.getJobPostingId()))
            throw new ApiException(MyError.JOB_ALREADY_APPLIED);

        if (!jobPosting.isActive())
            throw new ApiException(MyError.JOB_POSTING_INACTIVE);

        m_jobApplicationRepository.save(new JobApplication(user, jobPosting));
        LogUtil.log(email, "User applied for a job posting", LogType.SUCCESSFUL);
    }

    public Page<JobApplication> findAllJobApplication(int page, int size)
    {
        var pageable = PageRequest.of(page, size);
        return m_jobApplicationRepository.findAll(pageable);
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
                .orElseThrow(() -> new ApiException(MyError.JOB_NOT_FOUND));

        return m_jobApplicationRepository.findAllByJobPosting(jobPosting, pageable);
    }

    public Page<JobApplication> findJobApplicationsByStatus(String statusDTO, int page, int size)
    {
        var pageable = PageRequest.of(page, size);
        var status = checkAndGetStatus(statusDTO);

        return m_jobApplicationRepository.findAllByStatus(status, pageable);
    }

    public void approveJobApplication(long jobApplicationId)
    {
        var jobApplication = m_jobApplicationRepository.findById(jobApplicationId)
                .orElseThrow(() -> new ApiException(MyError.JOB_APPLICATION_NOT_FOUND));

        if (jobApplication.getStatus() == ApplicationStatus.APPROVED)
            throw new ApiException(MyError.JOB_APPLICATION_ALREADY_APPROVED);

        if (jobApplication.getStatus() == ApplicationStatus.REJECTED)
            throw new ApiException(MyError.JOB_APPLICATION_ALREADY_REJECTED);

        var jobPosting = jobApplication.getJobPosting();
        jobPosting.setActive(false);

        m_jobPostingRepository.save(jobPosting);

        jobApplication.setStatus(ApplicationStatus.APPROVED);
        m_jobApplicationRepository.save(jobApplication);

    }

    public void rejectJobApplication(long jobApplicationId)
    {
        var jobApplication = m_jobApplicationRepository.findById(jobApplicationId)
                .orElseThrow(() -> new ApiException(MyError.JOB_APPLICATION_NOT_FOUND));

        if (jobApplication.getStatus() == ApplicationStatus.APPROVED)
            throw new ApiException(MyError.JOB_APPLICATION_ALREADY_APPROVED);

        if (jobApplication.getStatus() == ApplicationStatus.REJECTED)
            throw new ApiException(MyError.JOB_APPLICATION_ALREADY_REJECTED);

        jobApplication.setStatus(ApplicationStatus.REJECTED);
        m_jobApplicationRepository.save(jobApplication);
    }
}
