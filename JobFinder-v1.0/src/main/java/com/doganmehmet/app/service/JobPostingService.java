package com.doganmehmet.app.service;

import com.doganmehmet.app.dto.jobposting.JobPostingDTO;
import com.doganmehmet.app.dto.jobposting.JobPostingRequestDTO;
import com.doganmehmet.app.enums.LogType;
import com.doganmehmet.app.exception.ApiException;
import com.doganmehmet.app.exception.MyError;
import com.doganmehmet.app.mapper.IJobPostingMapper;
import com.doganmehmet.app.repository.ICompanyRepository;
import com.doganmehmet.app.repository.IJobPostingRepository;
import com.doganmehmet.app.utility.LogUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class JobPostingService {
    private final IJobPostingRepository m_jobPostingRepository;
    private final IJobPostingMapper m_jobPostingMapper;
    private final ICompanyRepository m_companyRepository;

    public JobPostingService(IJobPostingRepository jobPostingRepository, IJobPostingMapper jobPostingMapper, ICompanyRepository companyRepository)
    {
        m_jobPostingRepository = jobPostingRepository;
        m_jobPostingMapper = jobPostingMapper;
        m_companyRepository = companyRepository;
    }

    public void save(JobPostingRequestDTO jobPostingRequestDTO)
    {
        var company = m_companyRepository.findByName(jobPostingRequestDTO.getCompanyName())
                .orElseThrow(() -> new ApiException(MyError.COMPANY_NOT_FOUND));

        var jobPosting = m_jobPostingMapper.toJobPosting(jobPostingRequestDTO);
        jobPosting.setCompany(company);
        company.getJobPostings().add(jobPosting);
        m_companyRepository.save(company);

        LogUtil.log(company.getName(), "Job posting saved", LogType.SUCCESSFUL);
        m_jobPostingMapper.toJobPostingDTO(m_jobPostingRepository.save(jobPosting));
    }

    public JobPostingDTO findJobPostingById(long jobPostingId)
    {
        return m_jobPostingMapper.toJobPostingDTO(m_jobPostingRepository.findById(jobPostingId)
                .orElseThrow(() -> new ApiException(MyError.JOB_NOT_FOUND)));

    }
    public Page<JobPostingDTO> findAllJobPostings(int page, int size)
    {
        var pageable = PageRequest.of(page, size, Sort.by("title").ascending());

        return m_jobPostingMapper.toJobPostingDTOPage(m_jobPostingRepository.findAll(pageable));
    }

    public Page<JobPostingDTO> findAllJobPostingsByCompany(long companyId, int page, int size)
    {
        var pageable = PageRequest.of(page, size, Sort.by("title").ascending());

        var company = m_companyRepository.findById(companyId)
                .orElseThrow(() -> new ApiException(MyError.COMPANY_NOT_FOUND));

        return m_jobPostingMapper.toJobPostingDTOPage(m_jobPostingRepository.findAllByCompany(company, pageable));
    }

    public Page<JobPostingDTO> searchAllJobPostings(String keyword, int page, int size)
    {
        var pageable = PageRequest.of(page, size, Sort.by("title").ascending());

        var jobPostings = m_jobPostingRepository
                .findAllByTitleOrDescriptionOrRequiredSkillsContainsIgnoreCase(keyword, keyword, keyword, pageable);

        return m_jobPostingMapper.toJobPostingDTOPage(jobPostings);
    }

    public Page<JobPostingDTO> findAllJobPostingsByExperience(int experience, int page, int size)
    {
        var pageable = PageRequest.of(page, size, Sort.by("minExperience").ascending());
        var jobPostings = m_jobPostingRepository.findAllByMinExperienceGreaterThanEqual(experience, pageable);
        return m_jobPostingMapper.toJobPostingDTOPage(jobPostings);
    }

    public Page<JobPostingDTO> findAllActiveJobPostings(int page, int size)
    {
        var pageable = PageRequest.of(page, size, Sort.by("title").ascending());

        var jobPostings = m_jobPostingRepository.findAllByActive(true, pageable);
        return m_jobPostingMapper.toJobPostingDTOPage(jobPostings);
    }

    public Page<JobPostingDTO> findAllJobPostingsByLocation(String location, int page, int size)
    {
        var pageable = PageRequest.of(page, size, Sort.by("title").ascending());
        var jobPostings = m_jobPostingRepository.findAllByLocation(location, pageable);
        return m_jobPostingMapper.toJobPostingDTOPage(jobPostings);
    }
}
