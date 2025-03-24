package com.doganmehmet.app.security;

import com.doganmehmet.app.enums.LogType;
import com.doganmehmet.app.exception.ApiException;
import com.doganmehmet.app.exception.MyError;
import com.doganmehmet.app.repository.IUserRepository;
import com.doganmehmet.app.utility.LogUtil;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final IUserRepository m_userRepository;
    private final BCryptPasswordEncoder m_passwordEncoder;

    public CustomAuthenticationProvider(IUserRepository userRepository, BCryptPasswordEncoder passwordEncoder)
    {
        m_userRepository = userRepository;
        m_passwordEncoder = passwordEncoder;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException
    {
        var user = m_userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ApiException(MyError.USER_NOT_FOUND));

        if (user.isBlocked()) {
            LogUtil.log(user.getEmail(), "User is blocked!", LogType.ACCOUNT_LOCKED);
            throw new ApiException(MyError.USER_BLOCKED);
        }

        if (!m_passwordEncoder.matches(authentication.getCredentials().toString(), user.getPassword())) {
            user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);

            if (user.getFailedLoginAttempts() >= 3)
                user.setBlocked(true);

            m_userRepository.save(user);
            LogUtil.log(user.getEmail(), "Incorrect password!", LogType.INCORRECT_PASSWORD);
            throw new ApiException(MyError.PASSWORD_INCORRECT, "Remaining attempts: " + (3 - user.getFailedLoginAttempts()));
        }

        user.setFailedLoginAttempts(0);
        m_userRepository.save(user);
        LogUtil.log(user.getEmail(), "Successfully logged in!", LogType.LOGIN);
        return new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword(), user.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication)
    {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
