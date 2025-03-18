package com.doganmehmet.app.exception;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(prefix = "m_")
public class ApiException extends RuntimeException {

    private final MyError m_myError;

    public ApiException(MyError myError)
    {
        super(myError.getErrorMessage());
        m_myError = myError;
    }

    public ApiException(MyError myError, Object... args)
    {
        super(String.format(myError.getErrorMessage(), args));
        m_myError = myError;
    }
}
