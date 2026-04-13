package com.abswitch.weblog.search.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Author：abSwitch
 * @url：
 * @date：2026-03-30 15:31
 * @Description：
 */
@ConfigurationProperties(prefix = "lucene")
@Component
@Data
public class LuceneProperties {

    /**
     * 索引存放的文件夹
     */
    private String indexDir;

}
