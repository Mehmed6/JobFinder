package com.doganmehmet.app.dto.jobapplication;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JobApplicationDTO {

    private long userId;
    private long jobPostingId;
    private String status;
}
