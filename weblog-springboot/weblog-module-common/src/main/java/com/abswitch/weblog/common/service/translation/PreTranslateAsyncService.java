package com.abswitch.weblog.common.service.translation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 异步触发预翻译。文章发布/分类标签创建时调用，不阻塞主流程。
 * <p>
 * 必须放在独立 Bean 中，否则 @Async 在同类方法调用时不生效（AOP 代理限制）。
 */
@Slf4j
@Component
public class PreTranslateAsyncService {

    @Autowired
    private TranslationService translationService;

    @Async
    public void preTranslateArticle(String title, String summary, String content) {
        try {
            if (title != null && !title.isBlank()) {
                translationService.translateAndCache(title, "zh", "en");
            }
            if (summary != null && !summary.isBlank()) {
                translationService.translateAndCache(summary, "zh", "en");
            }
            if (content != null && !content.isBlank()) {
                translationService.translateAndCache(content, "zh", "en");
            }
        } catch (Exception e) {
            log.error("[PreTranslate] 文章预翻译失败: {}", e.getMessage());
        }
    }

    @Async
    public void preTranslateText(String text) {
        try {
            if (text != null && !text.isBlank()) {
                translationService.translateAndCache(text, "zh", "en");
            }
        } catch (Exception e) {
            log.error("[PreTranslate] 文本预翻译失败: {}", e.getMessage());
        }
    }

    @Async
    public void preTranslateTexts(List<String> texts) {
        if (texts == null) return;
        for (String t : texts) {
            try {
                if (t != null && !t.isBlank()) {
                    translationService.translateAndCache(t, "zh", "en");
                }
            } catch (Exception e) {
                log.error("[PreTranslate] 文本预翻译失败: {}", e.getMessage());
            }
        }
    }
}
