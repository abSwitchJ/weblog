package com.abswitch.weblog.jwt.filter;

import cloud.tianai.captcha.application.ImageCaptchaApplication;
import cloud.tianai.captcha.spring.plugins.secondary.SecondaryVerificationApplication;
import com.abswitch.weblog.jwt.expection.CaptchaVerificationFailedException;
import com.abswitch.weblog.jwt.expection.UsernameOrPasswordNullException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.IOException;
import java.util.Objects;

/**
 * @Author：abSwitch
 * @url：
 * @date：2026-03-18 14:31
 * @Description：
 */
public class JwtAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
    /**
     * 指定用户登录的访问地址
     */
    public JwtAuthenticationFilter() {
        super(new AntPathRequestMatcher("/login", "POST"));
    }

    /**
     * 验证码校验器
     */
    private ImageCaptchaApplication imageCaptchaApplication;

    /**
     * 设置验证码校验器
     */
    public void setImageCaptchaApplication(ImageCaptchaApplication imageCaptchaApplication) {
        this.imageCaptchaApplication = imageCaptchaApplication;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        ObjectMapper mapper = new ObjectMapper();

        //解析提交的JSON数据
        JsonNode jsonNode = mapper.readTree(request.getInputStream());
        JsonNode usernameNode = jsonNode.get("username");
        JsonNode passwordNode = jsonNode.get("password");
        JsonNode captchaIdNode = jsonNode.get("captchaId");

        String username = usernameNode.textValue();
        String password = passwordNode.textValue();

        //判断用户名、密码是否为空
        if (passwordNode.isNull() || usernameNode.isNull()
                || StringUtils.isBlank(username) || StringUtils.isBlank(password)
        ) {
            throw new UsernameOrPasswordNullException("用户名或密码不能为空");
        }

        // 二次校验验证码
        if (Objects.nonNull(imageCaptchaApplication)) {
            // 验证码 ID
            String captchaId = captchaIdNode != null ? captchaIdNode.textValue() : null;
            if (StringUtils.isBlank(captchaId)) {
                throw new CaptchaVerificationFailedException("验证码 ID 不能为空");
            }

            // 执行二次校验
            boolean verified = false;
            if (imageCaptchaApplication instanceof SecondaryVerificationApplication) {
                verified = ((SecondaryVerificationApplication) imageCaptchaApplication).secondaryVerification(captchaId);
            }
            if (!verified) {
                throw new CaptchaVerificationFailedException("验证码校验失败，请重新验证");
            }
        }

        //将用户名、密码封装到token中
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
                = new UsernamePasswordAuthenticationToken(username, password);

        return getAuthenticationManager().authenticate(usernamePasswordAuthenticationToken);
    }
}
