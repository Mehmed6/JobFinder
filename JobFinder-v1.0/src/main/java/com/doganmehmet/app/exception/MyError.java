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
    USER_BLOCKED("1005", "Your account has been locked due to 3 incorrect password attempts!"),
    COMPANY_NOT_FOUND("1006", "Company not found!"),
    EMAIL_INCORRECT("1007", "Email incorrect! %s"),
    JOB_NOT_FOUND("1008", "Job not found!"),
    JOB_ALREADY_APPLIED("1009", "You have already applied for this job!"),
    COMPANY_ALREADY_EXISTS("1010", "Company already exists!"),
    INVALID_STATUS("1011", "Invalid status!"),

    ;

    private final String m_errorCode;
    private final String m_errorMessage;
}
