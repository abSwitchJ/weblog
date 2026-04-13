package com.abswitch.weblog.admin.config;


import com.abswitch.weblog.jwt.config.JwtAuthenticationSecurityConfig;
import com.abswitch.weblog.jwt.filter.TokenAuthenticationFilter;
import com.abswitch.weblog.jwt.handler.RestAccessDeniedHandler;
import com.abswitch.weblog.jwt.handler.RestAuthenticationEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


/**
 * @Author：abSwitch
 * @url：
 * @date：2026-03-18 12:56
 * @Description：
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
                            // 启用 @PreAuthorize / @PostAuthorize 等
public class WebSecurityConfig {

    @Autowired
    private JwtAuthenticationSecurityConfig jwtAuthenticationSecurityConfig;


    @Autowired
    private RestAuthenticationEntryPoint authEntryPoint;
    @Autowired
    private RestAccessDeniedHandler deniedHandler;

    @Autowired
    private TokenAuthenticationFilter tokenAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {



        http.csrf(crfs -> crfs.disable()) // 禁用 csrf
                .formLogin(form -> form.disable()) // 禁用表单登录
                .with(jwtAuthenticationSecurityConfig, configurer -> {}) //设置用户登录认证相关配置
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/admin/**").authenticated() // 认证所有以 /admin 为前缀的 URL 资源
                        .anyRequest().permitAll()   // 其他都需要放行，无需认证
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(authEntryPoint) // 处理用户未登录访问受保护的资源的情况
                        .accessDeniedHandler(deniedHandler) // 处理登录成功后访问受保护的资源，但是权限不够的情况
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 前后端分离，无需创建会话
                )
                .addFilterBefore(tokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class) // 将 Token 校验过滤器添加到用户认证过滤器之前
                ;

        return http.build();
    }

}