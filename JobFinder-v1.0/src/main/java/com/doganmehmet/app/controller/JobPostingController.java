package com.doganmehmet.app.controller;

import com.doganmehmet.app.dto.jobposting.JobPostingRequestDTO;
import com.doganmehmet.app.repository.ICompanyRepository;
import com.doganmehmet.app.service.JobPostingService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/job/posting")
public class JobPostingController {

    private final JobPostingService m_jobPostingService;
    private final ICompanyRepository m_companyRepository;

    public JobPostingController(JobPostingService jobPostingService, ICompanyRepository companyRepository)
    {
        m_jobPostingService = jobPostingService;
        m_companyRepository = companyRepository;
    }

    @GetMapping("/save")
    public String showJobPostingSavePage(Model model)
    {
        model.addAttribute("jobPostingRequestDTO", new JobPostingRequestDTO());
        model.addAttribute("companies", m_companyRepository.findAll());
        return "jobPosting/saveJobPosting";
    }

    @PostMapping("/save")
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

    @GetMapping("/show/all")
    public String showAllJobPostings(@RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "5") int size, Model model)
    {
        model.addAttribute("jobPostingDTO", m_jobPostingService.findAllJobPostings(page, size));
        return "jobPosting/jobPostingAll";
    }

    @GetMapping("/show/all/by-company/{companyId}")
    public String showAllJobPostingsByCompanyId(@PathVariable long companyId,
                                                @RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "5") int size, Model model)
    {
        try {
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
        var jobPostingDTO = m_jobPostingService.findAllJobPostingsByLocation(location, page, size);

        if (jobPostingDTO.getTotalElements() == 0) {
            model.addAttribute("errorMessage", "No job postings found for the specified location!");
            return "error/errorPage";
        }

        model.addAttribute("jobPostingDTO", jobPostingDTO);
        return "jobPosting/jobPostingLocation";
    }
}
