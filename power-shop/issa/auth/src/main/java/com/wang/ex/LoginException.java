package com.wang.ex;

import org.springframework.security.core.AuthenticationException;

public class LoginException extends AuthenticationException {
    public LoginException(String msg) {
        super(msg);
    }
}
