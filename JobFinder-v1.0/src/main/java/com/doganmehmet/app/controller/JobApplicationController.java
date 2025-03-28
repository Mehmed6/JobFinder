package com.doganmehmet.app.controller;

import com.doganmehmet.app.dto.jobapplication.JobApplicationRequest;
import com.doganmehmet.app.enums.LogType;
import com.doganmehmet.app.service.JobApplicationService;
import com.doganmehmet.app.utility.LogUtil;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

        var email = SecurityContextHolder.getContext().getAuthentication().getName();

        try {
            m_jobApplicationService.save(jobApplicationRequest, email);
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
            var jobApplications = m_jobApplicationService.findJobApplicationsByUser(userId, page, size);

            if (jobApplications.getTotalElements() == 0) {
                model.addAttribute("errorMessage", "User has not applied for any job postings!");
                return "error/errorPage";
            }

            model.addAttribute("jobApplications", jobApplications);
        }
        catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "error/errorPage";
        }

        return "jobApplication/jobApplicationUser";
    }

    @GetMapping("/show/by-job/posting/{jobPostingId}")
    @PreAuthorize("hasRole('ADMIN')")
    public String showJobPostingJobApplications(@PathVariable long jobPostingId,
                                               @RequestParam(defaultValue = "0") int page,
                                               @RequestParam(defaultValue = "5") int size, Model model)
    {
        try {
            var jobApplications = m_jobApplicationService
                    .findJobApplicationsByJobPosting(jobPostingId, page, size);

            if (jobApplications.getTotalElements() == 0) {
                model.addAttribute("errorMessage", "No applications found for this job posting!");
                return "error/errorPage";
            }

            model.addAttribute("jobApplications", jobApplications);
        }
        catch (Exception ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return "error/errorPage";
        }

        return "jobApplication/jobApplicationJobPosting";
    }

    @GetMapping("/show/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public String showStatusJobApplications(@PathVariable String status,
                                            @RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "5") int size, Model model)
    {
        try {
            var jobApplications = m_jobApplicationService.findJobApplicationsByStatus(status, page, size);

            if (jobApplications.getTotalElements() == 0) {
                model.addAttribute("errorMessage", "No applications found for this job status!");
                return "error/errorPage";
            }
            model.addAttribute("jobApplications", jobApplications);
        }
        catch (Exception ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return "error/errorPage";
        }

        return "jobApplication/jobApplicationStatus";
    }

    @GetMapping("/show/all")
    @PreAuthorize("hasRole('ADMIN')")
    public String showAll(@RequestParam(defaultValue = "0") int page,
                          @RequestParam(defaultValue = "5") int size, Model model)
    {
        var jobApplications = m_jobApplicationService.findAllJobApplication(page, size);

        if (jobApplications.getTotalElements() == 0) {
            model.addAttribute("errorMessage", "No applications found!");
            return "error/errorPage";
        }

        model.addAttribute("jobApplications", jobApplications);
        return "jobApplication/jobApplicationAll";
    }

    @PostMapping("/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public String approved(@RequestParam long jobApplicationId,
                           RedirectAttributes redirectAttributes,
                           Model model)
    {
        try {
            m_jobApplicationService.approveJobApplication(jobApplicationId);
            redirectAttributes.addFlashAttribute("message", "Job Application Successfully Approved");
        }
        catch (Exception ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return "error/errorPage";
        }
        var email = SecurityContextHolder.getContext().getAuthentication().getName();
        LogUtil.log(email, "Job Application Approved", LogType.APPROVED);
        return "redirect:/job/application/show/all";
    }

    @PostMapping("/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public String rejected(@RequestParam long jobApplicationId,
                           RedirectAttributes redirectAttributes,
                           Model model)
    {
        try {
            m_jobApplicationService.rejectJobApplication(jobApplicationId);
            redirectAttributes.addFlashAttribute("errorMessage", "Job Application Successfully Rejected");
        }
        catch (Exception ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return "error/errorPage";
        }

        var email = SecurityContextHolder.getContext().getAuthentication().getName();
        LogUtil.log(email, "Job Application Rejected", LogType.REJECTED);
        return "redirect:/job/application/show/all";
    }
}
