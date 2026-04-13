package com.abswitch.weblog.admin.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * @Author：abSwitch
 * @url：
 * @date：2026-03-17 15:59
 * @Description： Knife4j 配置
 */
@Configuration
@Profile("dev") // 只在 dev 环境中开启
public class Knife4jAdminConfig {

    @Bean("adminApi")
    public GroupedOpenApi createApiDoc() {

        return GroupedOpenApi.builder()
                // 分组名称
                .group("Admin 后台接口")
                // 这里指定 Controller 扫描包路径
                .packagesToScan("com.abswitch.weblog.admin.controller")
                .pathsToMatch("/**")
                .addOpenApiCustomizer(openApi ->openApi.info(adminInfo())) // 显式使用后台 OpenAPI
                .build();
    }

    /**
     * 构建 API 信息
     * @return
     */
    private Info adminInfo() {
        return new Info()
                .title("Weblog 博客 Admin 后台接口文档") //标题
                .description("Weblog 是一款由 Spring Boot + Vue 3.2 + Vite 4.3 开发的前后端分离博客。") //描述
                .termsOfService("https://www.abswitchj.com/") // API 服务条款
                .contact(new Contact() // 联系人
                        .name("abSwitchJ")
                        .url("https://www.abswitchj.com")
                        .email("abswitchj@163.com"))
                .version("1.0")
                .license(new License().name("abSwitchJ 1.0").url("https://www.abswitchj.com")); // 设置 OpenAPI 文档的许可证信息
    }
}
