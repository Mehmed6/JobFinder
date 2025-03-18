package com.doganmehmet.app.entity;

import com.doganmehmet.app.role.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private long userId;
    private String firstname;
    private String lastname;
    private String email;
    private String password;
    private String phone;
    private String experience;
    private String skills;
    @Column(name = "years_of_experience")
    private int yearsOfExperience;

    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;
    @Column(name = "is_blocked")
    private boolean isBlocked = false;
    @Column(name = "failed_login_attempts")
    private int failedLoginAttempts = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "job_applications")
    private List<JobApplication> jobApplications;

}
