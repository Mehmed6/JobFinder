package com.doganmehmet.app.utility;

import com.doganmehmet.app.entity.User;
import com.doganmehmet.app.repository.IUserRepository;
import com.doganmehmet.app.role.Role;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;


@Component
public class AdminInitializer implements CommandLineRunner {

    private final IUserRepository m_userRepository;
    private final BCryptPasswordEncoder m_bCryptPasswordEncoder;

    public AdminInitializer(IUserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder)
    {
        m_userRepository = userRepository;
        m_bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public void run(String... args)
    {
        if (!m_userRepository.existsByRole(Role.ADMIN)) {
            var admin = new User();

            admin.setFirstname("Admin");
            admin.setLastname("Admin");
            admin.setEmail("admin@gmail.com");
            admin.setPassword(m_bCryptPasswordEncoder.encode("admin"));
            admin.setRole(Role.ADMIN);
            m_userRepository.save(admin);

            System.out.printf("Admin has been created. Email:%s password:%s", admin.getEmail(), admin.getPassword());

        }
    }
}
