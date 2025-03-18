package com.doganmehmet.app.service;

import com.doganmehmet.app.dto.LoginRequestDTO;
import com.doganmehmet.app.exception.ApiException;
import com.doganmehmet.app.security.CustomAuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class LoginService {

    private final CustomAuthenticationProvider m_customAuthenticationProvider;

    public LoginService(CustomAuthenticationProvider customAuthenticationProvider)
    {
        m_customAuthenticationProvider = customAuthenticationProvider;
    }

    public Authentication login(LoginRequestDTO request) throws ApiException
    {
        return m_customAuthenticationProvider.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(),
                        request.getPassword()));
    }
}
