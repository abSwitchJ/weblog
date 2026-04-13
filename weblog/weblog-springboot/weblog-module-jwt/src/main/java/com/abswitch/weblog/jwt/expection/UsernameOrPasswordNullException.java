package com.abswitch.weblog.jwt.expection;


import org.springframework.security.core.AuthenticationException;

/**
 * @Author：abSwitch
 * @url：
 * @date：2026-03-18 15:11
 * @Description：
 */
public class UsernameOrPasswordNullException extends AuthenticationException {
    public UsernameOrPasswordNullException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public UsernameOrPasswordNullException(String msg) {
        super(msg);
    }

}
