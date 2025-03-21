package com.doganmehmet.app.dto.jobposting;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JobPostingRequestDTO {

    @NotBlank(message = "Title cannot be empty!")
    private String title;
    @NotBlank(message = "Description cannot be empty!")
    private String description;
    @NotBlank(message = "Location cannot be empty!")
    private String location;
    @NotBlank(message = "Required skills cannot be empty!")
    private String requiredSkills;
    @NotNull(message = "Min experience cannot be empty!")
    private int minExperience;
    @NotBlank(message = "Company name cannot be empty!")
    private String companyName;
}
