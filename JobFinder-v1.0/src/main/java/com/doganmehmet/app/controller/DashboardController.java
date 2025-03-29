package com.doganmehmet.app.controller;

import com.doganmehmet.app.enums.Role;
import com.doganmehmet.app.exception.ApiException;
import com.doganmehmet.app.exception.MyError;
import com.doganmehmet.app.repository.IUserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    private final IUserRepository m_userRepository;

    public DashboardController(IUserRepository userRepository)
    {
        m_userRepository = userRepository;
    }

    @GetMapping
    public String dashboard(Model model)
    {
        var email = SecurityContextHolder.getContext().getAuthentication().getName();
        var user = m_userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(MyError.USER_NOT_FOUND));

        var fullName = user.getFirstname() + " " + user.getLastname();
        model.addAttribute("fullName", fullName.toUpperCase());

        if (user.getRole() == Role.ADMIN) {
            return "dashboard/adminDashboard";
        }

        model.addAttribute("userId", user.getUserId());

        return "dashboard/dashboard";
    }
}
