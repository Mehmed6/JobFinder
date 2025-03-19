package com.doganmehmet.app.service;

import com.doganmehmet.app.dto.register.RegisterRequestDTO;
import com.doganmehmet.app.exception.ApiException;
import com.doganmehmet.app.exception.MyError;
import com.doganmehmet.app.mapper.IUserMapper;
import com.doganmehmet.app.repository.IUserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class RegisterService {
    private final IUserRepository m_userRepository;
    private final BCryptPasswordEncoder m_passwordEncoder;
    private final IUserMapper m_userMapper;

    public RegisterService(IUserRepository userRepository, BCryptPasswordEncoder passwordEncoder, IUserMapper userMapper)
    {
        m_userRepository = userRepository;
        m_passwordEncoder = passwordEncoder;
        m_userMapper = userMapper;
    }

    public void register(RegisterRequestDTO registerRequestDTO)
    {
        if (m_userRepository.existsByEmail(registerRequestDTO.getEmail()))
            throw new ApiException(MyError.EMAIL_ALREADY_EXISTS);

        var user = m_userMapper.toUser(registerRequestDTO);
        user.setPassword(m_passwordEncoder.encode(registerRequestDTO.getPassword()));

        m_userRepository.save(user);
    }
}
