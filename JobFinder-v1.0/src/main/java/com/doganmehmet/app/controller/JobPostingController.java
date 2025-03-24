package com.doganmehmet.app.controller;

import com.doganmehmet.app.dto.jobposting.JobPostingRequestDTO;
import com.doganmehmet.app.exception.ApiException;
import com.doganmehmet.app.exception.MyError;
import com.doganmehmet.app.repository.ICompanyRepository;
import com.doganmehmet.app.repository.IUserRepository;
import com.doganmehmet.app.service.JobPostingService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/job/posting")
public class JobPostingController {

    private final JobPostingService m_jobPostingService;
    private final ICompanyRepository m_companyRepository;
    private final IUserRepository m_userRepository;

    public JobPostingController(JobPostingService jobPostingService, ICompanyRepository companyRepository, IUserRepository userRepository)
    {
        m_jobPostingService = jobPostingService;
        m_companyRepository = companyRepository;
        m_userRepository = userRepository;
    }

    private void setUser(Model model)
    {
        var email = SecurityContextHolder.getContext().getAuthentication().getName();
        var user = m_userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(MyError.USER_NOT_FOUND));

        model.addAttribute("userId", user.getUserId());
    }

    @GetMapping("/save")
    public String showJobPostingSavePage(Model model)
    {
        model.addAttribute("jobPostingRequestDTO", new JobPostingRequestDTO());
        model.addAttribute("companies", m_companyRepository.findAll());
        return "jobPosting/saveJobPosting";
    }

    @PostMapping("/save")
    @PreAuthorize("hasRole('ADMIN')")
    public String saveJobPosting(@Valid JobPostingRequestDTO jobPostingRequestDTO, BindingResult bindingResult,Model model)
    {
        var companies = m_companyRepository.findAll();
        model.addAttribute("companies", companies);

        if (bindingResult.hasErrors())
            return "jobPosting/saveJobPosting";

        try {
            m_jobPostingService.save(jobPostingRequestDTO);
        }
        catch (Exception ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return "error/errorPage";
        }

        model.addAttribute("jobPostingRequestDTO", jobPostingRequestDTO);
        model.addAttribute("message", "Job posting was saved successfully");

        return "jobPosting/saveJobPosting";
    }

    @GetMapping("/show/{jobPostingId}")
    public String showJobPosting(@PathVariable long jobPostingId, Model model)
    {
        try {
            setUser(model);
            var jobPostingDTO = m_jobPostingService.findJobPostingById(jobPostingId);
            model.addAttribute("jobPostingDTO", jobPostingDTO);
            model.addAttribute("jobPostingId", jobPostingId);
        }
        catch (Exception ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return "error/errorPage";
        }

        return "jobPosting/jobPosting";
    }

    @GetMapping("/show/all")
    public String showAllJobPostings(@RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "5") int size, Model model)
    {
        setUser(model);

        model.addAttribute("jobPostingDTO", m_jobPostingService.findAllJobPostings(page, size));
        return "jobPosting/jobPostingAll";
    }

    @GetMapping("/show/all/by-company/{companyId}")
    public String showAllJobPostingsByCompanyId(@PathVariable long companyId,
                                                @RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "5") int size, Model model)
    {
        try {
            setUser(model);
            var jobPostingDTO = m_jobPostingService.findAllJobPostingsByCompany(companyId, page, size);

            if (jobPostingDTO.getTotalElements() == 0) {
                model.addAttribute("errorMessage", "No job postings found for this company");
                return "error/errorPage";
            }

            model.addAttribute("jobPostingDTO", jobPostingDTO);
            return "jobPosting/jobPostingCompany";
        }
        catch (Exception ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return "error/errorPage";
        }
    }

    @GetMapping("/search/{keyword}")
    public String searchJobPostings(@PathVariable String keyword,
                                    @RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "5") int size, Model model)
    {
        setUser(model);

        var jobPostingDTO = m_jobPostingService.searchAllJobPostings(keyword, page, size);

        if (jobPostingDTO.getTotalElements() == 0) {
            model.addAttribute("errorMessage", "No Job Posting found with the entered qualifications!");
            return "error/errorPage";
        }

        model.addAttribute("jobPostingDTO", jobPostingDTO);
        return "jobPosting/jobPostingSearch";
    }

    @GetMapping("/show/all/experience/{minExperience}")
    public String showAllJobPostingsByExperience(@PathVariable int minExperience,
                                                 @RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "5") int size, Model model)
    {
        setUser(model);

        var jobPostingDTO = m_jobPostingService.findAllJobPostingsByExperience(minExperience, page, size);

        if (jobPostingDTO.getTotalElements() == 0) {
            model.addAttribute("errorMessage", "No job postings found matching the minimum required experience!");
            return "error/errorPage";
        }

        model.addAttribute("jobPostingDTO", jobPostingDTO);
        return "jobPosting/jobPostingExperience";
    }

    @GetMapping("/show/all/active")
    public String showAllActiveJocPostings(@RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "5") int size, Model model)
    {
        setUser(model);

        var jobPostingDTO = m_jobPostingService.findAllActiveJobPostings(page, size);

        if (jobPostingDTO.getTotalElements() == 0) {
            model.addAttribute("errorMessage", "No Active job posting found!");
            return "error/errorPage";
        }

        model.addAttribute("jobPostingDTO", jobPostingDTO);
        return "jobPosting/jobPostingActive";
    }

    @GetMapping("/show/all/by-location/{location}")
    public String showAllJobPostingsByLocation(@PathVariable String location,
                                               @RequestParam(defaultValue = "0") int page,
                                               @RequestParam(defaultValue = "5") int size, Model model)
    {
        setUser(model);

        var jobPostingDTO = m_jobPostingService.findAllJobPostingsByLocation(location, page, size);

        if (jobPostingDTO.getTotalElements() == 0) {
            model.addAttribute("errorMessage", "No job postings found for the specified location!");
            return "error/errorPage";
        }

        model.addAttribute("jobPostingDTO", jobPostingDTO);
        return "jobPosting/jobPostingLocation";
    }
}
