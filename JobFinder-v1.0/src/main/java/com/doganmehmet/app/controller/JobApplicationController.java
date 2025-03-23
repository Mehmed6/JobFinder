package com.doganmehmet.app.controller;

import com.doganmehmet.app.dto.jobapplication.JobApplicationRequest;
import com.doganmehmet.app.service.JobApplicationService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/job/application")
public class JobApplicationController {

    private final JobApplicationService m_jobApplicationService;

    public JobApplicationController(JobApplicationService jobApplicationService)
    {
        m_jobApplicationService = jobApplicationService;
    }

    @GetMapping("/save")
    public String showSavePage(Model model)
    {
        model.addAttribute("jobApplicationRequest", new JobApplicationRequest());
        return "jobApplication/saveJobApplication";
    }

    @PostMapping("/save")
    public String save(@Valid JobApplicationRequest jobApplicationRequest, BindingResult bindingResult, Model model)
    {
        if (bindingResult.hasErrors())
            return "jobApplication/saveJobApplication";

        try {
            m_jobApplicationService.save(jobApplicationRequest);
        }
        catch (Exception ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return "error/errorPage";
        }

        model.addAttribute("message", "Job Application Successfully Saved");
        return "jobApplication/saveJobApplication";
    }

    @GetMapping("/show/by-user/{userId}")
    public String showUserJobApplications(@PathVariable long userId,
                                          @RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "5") int size, Model model)
    {
        try {
            var jobApplicationDTO = m_jobApplicationService.findJobApplicationsByUser(userId, page, size);

            if (jobApplicationDTO.getTotalElements() == 0) {
                model.addAttribute("errorMessage", "User has not applied for any job postings!");
                return "error/errorPage";
            }

            model.addAttribute("jobApplicationDTO", jobApplicationDTO);
        }
        catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "error/errorPage";
        }

        return "jobApplication/jobApplicationUser";
    }

    @GetMapping("/show/by-job/posting/{jobPostingId}")
    public String showJobPostingJobApplications(@PathVariable long jobPostingId,
                                               @RequestParam(defaultValue = "0") int page,
                                               @RequestParam(defaultValue = "5") int size, Model model)
    {
        try {
            var jobApplicationDTO = m_jobApplicationService
                    .findJobApplicationsByJobPosting(jobPostingId, page, size);

            if (jobApplicationDTO.getTotalElements() == 0) {
                model.addAttribute("errorMessage", "No applications found for this job posting!");
                return "error/errorPage";
            }

            model.addAttribute("jobApplicationDTO", jobApplicationDTO);
        }
        catch (Exception ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return "error/errorPage";
        }

        return "jobApplication/jobApplicationJobPosting";
    }

    @GetMapping("/show/status/{status}")
    public String showStatusJobApplications(@PathVariable String status,
                                            @RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "5") int size, Model model)
    {
        try {
            var jobApplicationDTO = m_jobApplicationService.findJobApplicationsByStatus(status, page, size);

            if (jobApplicationDTO.getTotalElements() == 0) {
                model.addAttribute("errorMessage", "No applications found for this job status!");
                return "error/errorPage";
            }
            model.addAttribute("jobApplicationDTO", jobApplicationDTO);
        }
        catch (Exception ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return "error/errorPage";
        }

        return "jobApplication/jobApplicationStatus";
    }
}
