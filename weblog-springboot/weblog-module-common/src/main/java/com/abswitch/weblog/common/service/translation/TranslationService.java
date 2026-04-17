package com.abswitch.weblog.common.service.translation;

import java.util.List;
import java.util.Map;

/**
 * 翻译服务：调用百度翻译并将结果缓存到 t_translation_cache。
 * 设计目标：发布/更新时异步预翻译，前端切换语言时直接读缓存（必命中）。
 */
public interface TranslationService {

    /**
     * 翻译并缓存（若已有缓存直接返回）。
     * 失败返回 null，不写缓存，不抛异常给上层（异步调用场景常见）。
     */
    String translateAndCache(String sourceText, String sourceLang, String targetLang);

    /**
     * 仅查缓存，不触发实时翻译。命中返回译文，未命中返回 null。
     */
    String getCachedOrNull(String sourceText, String sourceLang, String targetLang);

    /**
     * 批量查缓存。返回 Map<sourceText, translatedText>。
     * 缓存未命中的源文本不会出现在返回 Map 中（前端 fallback 显示原文）。
     */
    Map<String, String> getTranslations(List<String> sourceTexts, String sourceLang, String targetLang);
}
