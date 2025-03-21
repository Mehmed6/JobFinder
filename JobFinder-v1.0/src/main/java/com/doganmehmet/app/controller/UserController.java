package com.doganmehmet.app.controller;

import com.doganmehmet.app.dto.user.UpdateUserDTO;
import com.doganmehmet.app.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/user")
public class UserController {

    private final UserService m_userService;

    public UserController(UserService userService)
    {
        m_userService = userService;
    }

    @GetMapping("/show/{userId}")
    public String showUser(@PathVariable long userId, Model model)
    {
        try {
            model.addAttribute("user", m_userService.findUser(userId));
        }
        catch (Exception ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return "error/errorPage";
        }

        return "user/user";
    }

    @GetMapping("/update")
    public String showUpdateUserPage(Model model)
    {
        model.addAttribute("updateUserDTO", new UpdateUserDTO());
        return "user/updateUser";
    }

    @PostMapping("/update")
    public String updateUser(@Valid UpdateUserDTO updateUserDTO, BindingResult bindingResult, Model model)
    {
        if (bindingResult.hasErrors())
            return "user/updateUser";

        try {
            model.addAttribute("updateUserDTO", m_userService.updateUser(updateUserDTO));
            model.addAttribute("message", "User updated successfully");
        }
        catch (Exception ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return "error/errorPage";
        }

        return "user/updateUser";
    }

    @GetMapping("/experience")
    public String showUsersPage(@RequestParam(required = false, defaultValue = "0") int minExperience,
                                @RequestParam(required = false, defaultValue = "0") int maxExperience,
                                @RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "5") int size, Model model)
    {

        var users = m_userService.findByExperienceYears(minExperience, maxExperience, page, size);

        if (users.getTotalElements() == 0) {
            model.addAttribute("message", "No users found");
            return "error/errorPage";
        }

        model.addAttribute("users", users);
        model.addAttribute("minExperience", minExperience);
        model.addAttribute("maxExperience", maxExperience);
        return "user/experienceBetween";
    }

    @GetMapping("/all")
    public String showAllUsersPage(@RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "5") int size,
                                   Model model)
    {
        model.addAttribute("users", m_userService.findAll(page, size));
        return "user/allUsers";
    }

    @GetMapping("/all/company/{companyId}")
    public String showAllUsersByCompany(@PathVariable long companyId,
                                        @RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "5") int size,
                                        Model model)
    {
        try {
            var users = m_userService.findAllUserByCompany(companyId, page, size);

            if (users.getTotalElements() == 0) {
                model.addAttribute("errorMessage", "No users found for this company");
                return "error/errorPage";
            }
            model.addAttribute("users", users);
            return "user/allUsersCompany";
        }
        catch (Exception ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return "error/errorPage";
        }

    }

    @GetMapping("/search/{keyword}")
    public String searchUsers(@PathVariable String keyword,
                              @RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "5") int size,
                              Model model)
    {
        var users = m_userService.searchUsers(keyword, page, size);

        if (users.getTotalElements() == 0) {
            model.addAttribute("errorMessage", "No user found with the entered qualifications.");
            return "error/errorPage";
        }

        model.addAttribute("users", users);
        model.addAttribute("keyword", keyword);
        return "user/searchUsers";
    }

    @GetMapping("/job/posting/{userId}")
    public String showJobPostingPage(@PathVariable long userId, Model model)
    {
        try {
            var jobs = m_userService.getRecommendedJobs(userId);

            if (jobs.isEmpty()) {
                model.addAttribute("errorMessage", "No jobs found");
                return "user/userAllJobPosting";
            }

            model.addAttribute("jobPostingDTO", jobs);
            return "user/userAllJobPosting";
        }
        catch (Exception ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return "error/errorPage";
        }
    }

}
