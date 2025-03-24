package com.doganmehmet.app.service;

import com.doganmehmet.app.dto.jobapplication.JobApplicationRequest;
import com.doganmehmet.app.dto.jobposting.JobPostingDTO;
import com.doganmehmet.app.dto.user.UpdateUserDTO;
import com.doganmehmet.app.dto.user.UserDTO;
import com.doganmehmet.app.entity.JobPosting;
import com.doganmehmet.app.entity.User;
import com.doganmehmet.app.enums.LogType;
import com.doganmehmet.app.exception.ApiException;
import com.doganmehmet.app.exception.MyError;
import com.doganmehmet.app.mapper.IJobPostingMapper;
import com.doganmehmet.app.mapper.IUserMapper;
import com.doganmehmet.app.repository.ICompanyRepository;
import com.doganmehmet.app.repository.IJobPostingRepository;
import com.doganmehmet.app.repository.IUserRepository;
import com.doganmehmet.app.utility.LogUtil;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
    private final IJobPostingRepository m_jobPostingRepository;
    private final IJobPostingMapper m_jobPostingMapper;

    public UserService(IUserRepository userRepository,
                       IUserMapper userMapper,
                       ICompanyRepository companyRepository,
                       BCryptPasswordEncoder passwordEncoder,
                       IJobPostingRepository jobPostingRepository,
                       IJobPostingMapper jobPostingMapper)
    {
        m_userRepository = userRepository;
        m_userMapper = userMapper;
        m_companyRepository = companyRepository;
        m_passwordEncoder = passwordEncoder;
        m_jobPostingRepository = jobPostingRepository;
        m_jobPostingMapper = jobPostingMapper;
    }

    private User updateUser(User user, UpdateUserDTO updateUserDTO)
    {
        user.setFirstname(updateUserDTO.getFirstname());
        user.setLastname(updateUserDTO.getLastname());
        user.setPhone(updateUserDTO.getPhone());
        user.setExperience(updateUserDTO.getExperience());
        user.setYearsOfExperience(updateUserDTO.getYearsOfExperience());
        user.setSkills(updateUserDTO.getSkills());

        return m_userRepository.save(user);
    }

    private boolean isJobAlreadyAppliedByUser(User user, JobPosting jobPosting)
    {
        return user.getJobApplications().stream()
                .anyMatch(jobApp -> jobApp.getJobPosting().equals(jobPosting));
    }

    private boolean checkIfMatch(User user, JobPosting jobPosting)
    {
        var jobSkills = jobPosting.getRequiredSkills().split("[, ]+");
        var userSkills = user.getSkills().split("[, ]+");

        return Arrays.stream(jobSkills)
                .anyMatch(j -> Arrays.stream(userSkills).map(String::toUpperCase)
                        .anyMatch(s -> s.equalsIgnoreCase(j)));
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

    public Page<UserDTO> findAllUserByCompany(long companyId, int page, int size)
    {
        var pageable = PageRequest.of(page, size, Sort.by("firstname").ascending());

        var company = m_companyRepository.findById(companyId)
                .orElseThrow(() -> new ApiException(MyError.COMPANY_NOT_FOUND));

        return m_userMapper.toUserDTOPage(m_userRepository.findAllByCompany(company, pageable));
    }

    public UpdateUserDTO updateUser(UpdateUserDTO updateUserDTO)
    {
        var user = m_userRepository.findByEmail(updateUserDTO.getEmail())
                .orElseThrow(() -> new ApiException(MyError.USER_NOT_FOUND));

        if (!m_passwordEncoder.matches(updateUserDTO.getPassword(), user.getPassword()))
            throw new ApiException(MyError.PASSWORD_INCORRECT, "Please try again!");

        LogUtil.log(user.getEmail(), "User updated successfully", LogType.SUCCESSFUL);
        return m_userMapper.toUpdateUserDTO(updateUser(user, updateUserDTO));
    }

    public Page<UserDTO> searchUsers(String keyword, int page, int size)
    {
        var pageable = PageRequest.of(page, size);

        var users = m_userRepository
                .findAllBySkillsOrExperienceContainsIgnoreCase(keyword, keyword, pageable);

        return m_userMapper.toUserDTOPage(users);
    }

    public void applyForJob(JobApplicationRequest request)
    {
        var user = m_userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ApiException(MyError.USER_NOT_FOUND));

        var job = m_jobPostingRepository.findById(request.getJobPostingId())
                .orElseThrow(() -> new ApiException(MyError.JOB_NOT_FOUND));

        if (isJobAlreadyAppliedByUser(user, job))
            throw new ApiException(MyError.JOB_ALREADY_APPLIED);

        LogUtil.log(user.getEmail(), "Applying job", LogType.SUCCESSFUL);

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

    @Transactional
    public void deleteUser(long userId)
    {
        var user = m_userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(MyError.USER_NOT_FOUND));

        LogUtil.log(user.getEmail(), "User deleted successfully", LogType.SUCCESSFUL);
        m_userRepository.deleteById(userId);
    }
}
