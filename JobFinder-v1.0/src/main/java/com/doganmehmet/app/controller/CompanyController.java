package com.doganmehmet.app.controller;

import com.doganmehmet.app.dto.company.CompanyRequestDTO;
import com.doganmehmet.app.exception.ApiException;
import com.doganmehmet.app.service.CompanyService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/company")
public class CompanyController {
    private final CompanyService m_companyService;

    public CompanyController(CompanyService companyService)
    {
        m_companyService = companyService;
    }

    @GetMapping("/save")
    public String showCompanySavePage(Model model)
    {
        model.addAttribute("companyRequestDTO", new CompanyRequestDTO());
        return "company/saveCompany";
    }

    @PostMapping("/save")
    public String saveCompany(@Valid CompanyRequestDTO companyRequestDTO, BindingResult bindingResult, Model model)
    {
        if (bindingResult.hasErrors())
            return "company/saveCompany";

        try {
            m_companyService.save(companyRequestDTO);
        }
        catch (ApiException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return "error/errorPage";
        }

        model.addAttribute("companyRequestDTO", companyRequestDTO);
        model.addAttribute("message", "Company saved successfully");
        return "company/saveCompany";
    }

    @GetMapping("/all")
    public String showAllCompanies(@RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "5") int size,
                                   Model model)
    {
        model.addAttribute("companies", m_companyService.findAllCompany(page, size));
        return "company/allCompanies";
    }

    @GetMapping("/all/users/{companyId}")
    public String showAllCompanyUsers(@PathVariable int companyId,
                                      @RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "5") int size,
                                      Model model)
    {
        try {
            var users = m_companyService.getAllUsersByCompanyId(companyId, page, size);

            if (users.getTotalElements() == 0) {
                model.addAttribute("errorMessage", "No users found for this company");
                return "error/errorPage";
            }
            model.addAttribute("users", users);
            model.addAttribute("companyId", companyId);
        }
        catch (Exception ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return "error/errorPage";
        }

        return "company/allCompanyUsers";
    }

    @GetMapping("/show/{companyId}")
    public String showCompany(@PathVariable int companyId, Model model)
    {
        try {
            model.addAttribute("company", m_companyService.findCompanyById(companyId));
        }
        catch (Exception ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return "error/errorPage";
        }

        return "company/company";
    }
}
