package com.doganmehmet.app.dto.jobposting;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JobPostingDTO {
    private long jobPostingId;
    private String title;
    private String description;
    private String location;
    private String requiredSkills;
    private int minExperience;
    private boolean isActive = true;
    private String companyName;
}
