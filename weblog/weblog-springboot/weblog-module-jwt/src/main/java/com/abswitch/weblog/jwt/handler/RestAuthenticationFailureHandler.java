package com.abswitch.weblog.jwt.handler;

import com.abswitch.weblog.common.emuns.ResponseCodeEnum;
import com.abswitch.weblog.common.utils.Response;
import com.abswitch.weblog.jwt.expection.CaptchaVerificationFailedException;
import com.abswitch.weblog.jwt.expection.UsernameOrPasswordNullException;
import com.abswitch.weblog.jwt.utils.ResultUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @Author：abSwitch
 * @url：
 * @date：2026-03-18 22:09
 * @Description：
 */

@Component
@Slf4j
public class RestAuthenticationFailureHandler implements AuthenticationFailureHandler {


    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {

        log.warn("AuthenticationException：", exception);

        if (exception instanceof UsernameOrPasswordNullException) {
            //用户名或密码为空
            ResultUtil.fail(response, Response.fail(exception.getMessage()));
        } else if (exception instanceof BadCredentialsException) {
            //用户名或密码错误
            ResultUtil.fail(response, Response.fail(ResponseCodeEnum.USERNAME_OR_PWD_WRROR));
        } else if (exception instanceof CaptchaVerificationFailedException) {
            // 行为验证码错误
            ResultUtil.fail(response, Response.fail(exception.getMessage()));
        }

        //登录失败
        ResultUtil.fail(response, Response.fail(ResponseCodeEnum.LOGIN_FAIL));
    }
}
