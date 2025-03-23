package com.doganmehmet.app.mapper;

import com.doganmehmet.app.dto.jobapplication.JobApplicationDTO;
import com.doganmehmet.app.entity.JobApplication;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

@Mapper(implementationName = "JobApplicationMapperImpl", componentModel = "spring")
public interface IJobApplicationMapper {

    JobApplicationDTO toJobApplicationDTO(JobApplication jobApplication);

    default Page<JobApplicationDTO> toJobApplicationDTOPage(Page<JobApplication> jobApplications)
    {
        return jobApplications.map(this::toJobApplicationDTO);
    }
}
