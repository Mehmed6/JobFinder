package com.doganmehmet.app.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@AllArgsConstructor
@Accessors(prefix = "m_")
public enum MyError {

    GENERAL_ERROR("1000", "General error: %s"),
    USER_NOT_FOUND("1001", "User not found!"),
    USER_ALREADY_EXISTS("1002", "User already exists!"),
    EMAIL_ALREADY_EXISTS("1003", "Email already exists!"),
    PASSWORD_INCORRECT("1004", "Password incorrect! %s"),

    ;

    private final String m_errorCode;
    private final String m_errorMessage;
}
