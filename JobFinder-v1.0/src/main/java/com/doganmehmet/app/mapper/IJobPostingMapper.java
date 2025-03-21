package com.doganmehmet.app.mapper;

import com.doganmehmet.app.dto.jobposting.JobPostingDTO;
import com.doganmehmet.app.dto.jobposting.JobPostingRequestDTO;
import com.doganmehmet.app.entity.JobPosting;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(implementationName = "JobPostingMapperImpl", componentModel = "spring")
public interface IJobPostingMapper {

    JobPosting toJobPosting(JobPostingRequestDTO jobPostingRequestDTO);

    @Mapping(source = "company.name", target = "companyName")
    JobPostingDTO toJobPostingDTO(JobPosting jobPosting);


    List<JobPostingDTO> toJobPostingDTOList(List<JobPosting> jobPostings);

    default Page<JobPostingDTO> toJobPostingDTOPage(Page<JobPosting> jobPostings)
    {
        return jobPostings.map(this::toJobPostingDTO);
    }

}
