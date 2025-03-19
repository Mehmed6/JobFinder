package com.doganmehmet.app.dto.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {

    private String firstname;
    private String lastname;
    private String email;
    private String phone;
    private String experience;
    private String skills;
    private int yearsOfExperience;
    private String companyName;

}
