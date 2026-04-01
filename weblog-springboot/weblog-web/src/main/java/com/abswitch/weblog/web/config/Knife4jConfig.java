package com.abswitch.weblog.web.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

/**
 * @Author：abSwitch
 * @url：
 * @date：2026-03-17 13:37
 * @Description：Knife4j 配置
 */
@Configuration
@Profile("dev") // 只在 dev 环境中开启
public class Knife4jConfig {

    @Bean("webApi")
    public GroupedOpenApi createApiDoc() {

        return GroupedOpenApi.builder()
                // 分组名称
                .group("Web 前台接口")
                // 这里指定 Controller 扫描包路径
                .packagesToScan("com.abswitch.weblog.web.controller")
                .pathsToMatch("/**")
                .addOpenApiCustomizer(openApi ->openApi.info(webInfo())) // 显式使用前台 OpenAPI Bean
                .build();
    }

    /**
     * 构建 API 信息
     * @return
     */
    private Info webInfo() {
        return new Info()
                .title("Weblog 博客前台接口文档") //标题
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

