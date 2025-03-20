package com.doganmehmet.app.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserDTO {

    @NotBlank(message = "Firstname cannot be empty!")
    private String firstname;
    @NotBlank(message = "Lastname cannot be empty!")
    private String lastname;
    @Email(message = "Email cannot be empty!")
    private String email;
    @NotBlank(message = "Password cannot be empty!")
    private String password;
    @NotBlank(message = "Phone cannot be empty!")
    private String phone;
    @NotBlank(message = "Experience cannot be empty!")
    private String experience;
    @NotBlank(message = "Skills cannot be empty!")
    private String skills;
    @NotNull(message = "Experience years cannot be empty!")
    private int yearsOfExperience;
}
