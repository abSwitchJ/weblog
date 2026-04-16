package com.abswitch.weblog.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "baidu.translate")
public class BaiduTranslateProperties {

    private String appId;

    private String secretKey;
}
