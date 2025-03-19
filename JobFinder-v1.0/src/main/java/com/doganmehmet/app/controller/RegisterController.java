package com.doganmehmet.app.controller;

import com.doganmehmet.app.dto.register.RegisterRequestDTO;
import com.doganmehmet.app.exception.ApiException;
import com.doganmehmet.app.service.RegisterService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/register")
public class RegisterController {

    private final RegisterService m_registerService;

    public RegisterController(RegisterService registerService)
    {
        m_registerService = registerService;
    }

    @GetMapping
    public String showRegisterPage(Model model)
    {
        model.addAttribute("registerRequestDTO", new RegisterRequestDTO());
        return "register/my-register";
    }

    @PostMapping
    public String register(@Valid RegisterRequestDTO registerRequestDTO, BindingResult bindingResult, RedirectAttributes redirectAttributes)
    {
        if (bindingResult.hasErrors())
            return "register/my-register";

        try {
            m_registerService.register(registerRequestDTO);
        }
        catch (ApiException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "register/my-register";
        }

        redirectAttributes.addFlashAttribute("message", "Registration successful! You can log in now.");
        return "redirect:/auth/login";
    }
}
