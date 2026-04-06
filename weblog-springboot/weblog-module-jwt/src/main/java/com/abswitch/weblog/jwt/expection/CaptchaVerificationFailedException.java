package com.abswitch.weblog.jwt.expection;

import org.springframework.security.core.AuthenticationException;

/**
 * @Author：abSwitch
 * @url：
 * @date：2026-04-06 11:52
 * @Description：
 */
public class CaptchaVerificationFailedException extends AuthenticationException {
    public CaptchaVerificationFailedException(String msg, Throwable cause) {
        super(msg, cause);
    }
    public CaptchaVerificationFailedException(String message) {
        super(message);
    }
}
