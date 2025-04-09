package com.doganmehmet.app.dto.company;


import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompanyRequestDTO {

    @NotBlank(message = "Company name cannot be empty!")
    private String name;
    @NotBlank(message = "Industry cannot be empty!")
    private String industry;
    @NotBlank(message = "Address cannot be empty!")
    private String address;
    @NotBlank(message = "Phone cannot be empty!")
    private String phone;
    @NotBlank(message = "Email cannot be empty!")
    private String email;
}
