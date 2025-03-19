package com.doganmehmet.app.service;

import com.doganmehmet.app.dto.jobposting.JobPostingDTO;
import com.doganmehmet.app.dto.register.RegisterRequestDTO;
import com.doganmehmet.app.dto.user.UserDTO;
import com.doganmehmet.app.entity.JobApplication;
import com.doganmehmet.app.entity.JobPosting;
import com.doganmehmet.app.entity.User;
import com.doganmehmet.app.exception.ApiException;
import com.doganmehmet.app.exception.MyError;
import com.doganmehmet.app.mapper.IJobPostingMapper;
import com.doganmehmet.app.mapper.IUserMapper;
import com.doganmehmet.app.repository.ICompanyRepository;
import com.doganmehmet.app.repository.IJobApplicationRepository;
import com.doganmehmet.app.repository.IJobPostingRepository;
import com.doganmehmet.app.repository.IUserRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class UserService {
    private final IUserRepository m_userRepository;
    private final IUserMapper m_userMapper;
    private final ICompanyRepository m_companyRepository;
    private final BCryptPasswordEncoder m_passwordEncoder;
    private final IJobApplicationRepository m_jobApplicationRepository;
    private final IJobPostingRepository m_jobPostingRepository;
    private final IJobPostingMapper m_jobPostingMapper;

    public UserService(IUserRepository userRepository, IUserMapper userMapper, ICompanyRepository companyRepository, BCryptPasswordEncoder passwordEncoder, IJobApplicationRepository jobApplicationRepository, IJobPostingRepository jobPostingRepository, IJobPostingMapper jobPostingMapper)
    {
        m_userRepository = userRepository;
        m_userMapper = userMapper;
        m_companyRepository = companyRepository;
        m_passwordEncoder = passwordEncoder;
        m_jobApplicationRepository = jobApplicationRepository;
        m_jobPostingRepository = jobPostingRepository;
        m_jobPostingMapper = jobPostingMapper;
    }

    private User updateUser(User user, RegisterRequestDTO registerRequestDTO)
    {
        user.setFirstname(registerRequestDTO.getFirstname());
        user.setLastname(registerRequestDTO.getLastname());
        user.setPhone(registerRequestDTO.getPhone());
        user.setExperience(registerRequestDTO.getExperience());
        user.setYearsOfExperience(registerRequestDTO.getYearsOfExperience());
        user.setSkills(registerRequestDTO.getSkills());

        return m_userRepository.save(user);
    }

    private boolean isJobAlreadyAppliedByUser(User user, JobPosting jobPosting)
    {
        return user.getJobApplications().stream()
                .anyMatch(jobApp -> jobApp.getJobPosting().equals(jobPosting));
    }

    public UserDTO findUser(long userId)
    {
        return m_userMapper.toUserDTO(m_userRepository.findById(userId)
                            .orElseThrow(() -> new ApiException(MyError.USER_NOT_FOUND)));
    }

    public Page<UserDTO> findByExperienceYears(int minExperience, int maxExperience, int page, int size)
    {
        var pageable = PageRequest.of(page, size, Sort.by("yearsOfExperience").ascending());
        return m_userMapper.toUserDTOPage(
                m_userRepository.findByYearsOfExperienceBetween(minExperience, maxExperience, pageable));
    }

    public Page<UserDTO> findAll(int page, int size)
    {
        var pageable = PageRequest.of(page, size, Sort.by("firstname").ascending());
        return m_userMapper.toUserDTOPage(m_userRepository.findAll(pageable));
    }

    public Page<UserDTO> findAllUserByCompany(long companyId, Pageable pageable)
    {
        var company = m_companyRepository.findById(companyId)
                .orElseThrow(() -> new ApiException(MyError.COMPANY_NOT_FOUND));

        return m_userMapper.toUserDTOPage(m_userRepository.findAllByCompany(company, pageable));
    }

    public UserDTO updateUser(long userId, RegisterRequestDTO registerRequestDTO)
    {
        var user = m_userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(MyError.USER_NOT_FOUND));

        if (!m_passwordEncoder.matches(registerRequestDTO.getPassword(), user.getPassword()))
            throw new ApiException(MyError.PASSWORD_INCORRECT, "Please try again!");

        if (!user.getEmail().equals(registerRequestDTO.getEmail()))
            throw new ApiException(MyError.EMAIL_INCORRECT, registerRequestDTO.getEmail());

        return m_userMapper.toUserDTO(updateUser(user, registerRequestDTO));
    }

    public Page<UserDTO> searchUsers(String keyword, int page, int size)
    {
        var pageable = PageRequest.of(page, size, Sort.by("firstname").ascending());

        return m_userMapper.toUserDTOPage(
                m_userRepository.
                        findAllBySkillsOrExperienceContainsIgnoreCase(keyword, keyword, pageable));
    }

    public Page<UserDTO> findExperienceYearsLessThanEqual(int maxExperience, int page, int size)
    {
        var pageable = PageRequest.of(page, size, Sort.by("yearsOfExperience").descending());

        return m_userMapper.toUserDTOPage(
                m_userRepository.findByYearsOfExperienceIsLessThanEqual(maxExperience, pageable));

    }

    public List<JobApplication> getUserJobApplications(long userId)
    {
        var user = m_userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(MyError.USER_NOT_FOUND));

        return user.getJobApplications();
    }

    public void applyForJob(long userId, long jobId)
    {
        var user = m_userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(MyError.USER_NOT_FOUND));

        var job = m_jobPostingRepository.findById(jobId)
                .orElseThrow(() -> new ApiException(MyError.JOB_NOT_FOUND));

        if (isJobAlreadyAppliedByUser(user, job))
            throw new ApiException(MyError.JOB_ALREADY_APPLIED);

        var jobApplication = new JobApplication();

        jobApplication.setUser(user);
        jobApplication.setJobPosting(job);

        user.getJobApplications().add(jobApplication);
        job.getJobApplications().add(jobApplication);

        m_userRepository.save(user);
        m_jobPostingRepository.save(job);
        m_jobApplicationRepository.save(jobApplication);
    }

    public List<JobPostingDTO> getRecommendedJobs(long userId)
    {
        var user = m_userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(MyError.USER_NOT_FOUND));

        var jobs = m_jobPostingRepository.findAll();

        var jobPostings = jobs.stream()
                .filter(job -> job.getMinExperience() <= user.getYearsOfExperience())
                .filter(job -> checkIfMatch(user, job))
                .toList();

        return m_jobPostingMapper.toJobPostingDTOList(jobPostings);
    }

    private boolean checkIfMatch(User user, JobPosting jobPosting)
    {
        var jobSkills = jobPosting.getRequiredSkills().split("[, ]+");
        var userSkills = user.getSkills().split("[, ]+");

        return Arrays.stream(jobSkills).anyMatch(j -> Arrays.asList(userSkills).contains(j));
    }

    @Transactional
    public void deleteUser(long userId)
    {
        m_userRepository.deleteById(userId);
    }
}
