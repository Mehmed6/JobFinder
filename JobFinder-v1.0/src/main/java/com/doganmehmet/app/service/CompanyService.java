package com.doganmehmet.app.service;

import com.doganmehmet.app.dto.company.CompanyDTO;
import com.doganmehmet.app.dto.company.CompanyRequestDTO;
import com.doganmehmet.app.dto.jobposting.JobPostingDTO;
import com.doganmehmet.app.dto.user.UserDTO;
import com.doganmehmet.app.exception.ApiException;
import com.doganmehmet.app.exception.MyError;
import com.doganmehmet.app.mapper.ICompanyMapper;
import com.doganmehmet.app.mapper.IJobPostingMapper;
import com.doganmehmet.app.mapper.IUserMapper;
import com.doganmehmet.app.repository.ICompanyRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class CompanyService {

    private final ICompanyRepository m_companyRepository;
    private final ICompanyMapper m_companyMapper;
    private final IUserMapper m_userMapper;
    private final IJobPostingMapper m_jobPostingMapper;

    public CompanyService(ICompanyRepository companyRepository, ICompanyMapper companyMapper, IUserMapper userMapper, IJobPostingMapper jobPostingMapper)
    {
        m_companyRepository = companyRepository;
        m_companyMapper = companyMapper;
        m_userMapper = userMapper;
        m_jobPostingMapper = jobPostingMapper;
    }

    public void save(CompanyRequestDTO companyRequest)
    {
        var check = m_companyRepository
                .findByNameOrEmailOrPhone(companyRequest.getName(), companyRequest.getEmail(), companyRequest.getPhone());

        if (check.isPresent())
            throw new ApiException(MyError.COMPANY_ALREADY_EXISTS);

        var savedCompany = m_companyRepository.save(m_companyMapper.toCompany(companyRequest));

        m_companyMapper.toCompanyDTO(savedCompany);
    }

    public Page<CompanyDTO> findAllCompany(int page, int size)
    {
        var pageable = PageRequest.of(page, size, Sort.by("name").ascending());

        return m_companyMapper.toCompanyDTOPage(m_companyRepository.findAll(pageable));
    }

    public Page<UserDTO> getAllUsersByCompanyId(long companyId ,int page, int size)
    {
        var company = m_companyRepository.findById(companyId)
                .orElseThrow(() -> new ApiException(MyError.COMPANY_NOT_FOUND));

        var pageable = PageRequest.of(page, size);

        return m_userMapper.toUserDTOPage(m_companyRepository.findUsersByCompany(company, pageable));

    }

    public Page<JobPostingDTO> getAllJobPostingsByCompanyId(long companyId, int page, int size)
    {
        var company = m_companyRepository.findById(companyId)
                .orElseThrow(() -> new ApiException(MyError.COMPANY_NOT_FOUND));

        var pageable = PageRequest.of(page, size);

        return m_jobPostingMapper.toJobPostingDTOPage(m_companyRepository.findJobPostingsByCompany(company, pageable));
    }

    public CompanyDTO findCompanyById(long companyId)
    {
        return m_companyMapper.toCompanyDTO(m_companyRepository.findById(companyId)
        .orElseThrow(() -> new ApiException(MyError.COMPANY_NOT_FOUND)));
    }
}
