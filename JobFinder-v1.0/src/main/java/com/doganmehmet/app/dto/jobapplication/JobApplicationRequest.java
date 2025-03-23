package com.doganmehmet.app.dto.jobapplication;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JobApplicationRequest {

    @NotNull(message = "User ID cannot be empty!")
    private long userId;
    @NotNull(message = "Job ID cannot be empty!")
    private long jobPostingId;
}
