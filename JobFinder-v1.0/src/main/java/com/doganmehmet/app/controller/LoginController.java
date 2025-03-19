package com.doganmehmet.app.controller;

import com.doganmehmet.app.dto.login.LoginRequestDTO;
import com.doganmehmet.app.exception.ApiException;
import com.doganmehmet.app.service.LoginService;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth/login")
public class LoginController {

    private final LoginService m_loginService;

    public LoginController(LoginService loginService)
    {
        m_loginService = loginService;
    }

    @GetMapping
    public String showLoginPage(Model model)
    {
        model.addAttribute("loginRequestDTO", new LoginRequestDTO());
        return "login/my-login";
    }

    @PostMapping
    public String login(LoginRequestDTO loginRequestDTO, BindingResult bindingResult ,Model model, HttpSession session)
    {
        if (bindingResult.hasErrors())
            return "login/my-login";

        try {
            var auth = m_loginService.login(loginRequestDTO);

            SecurityContextHolder.getContext().setAuthentication(auth);
            session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

            return "redirect:/dashboard";

        } catch (ApiException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return "login/my-login";
        }
    }
}
