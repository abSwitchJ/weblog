package com.abswitch.weblog.common.service.translation.impl;

import com.abswitch.weblog.common.config.BaiduTranslateProperties;
import com.abswitch.weblog.common.domain.dos.TranslationCacheDO;
import com.abswitch.weblog.common.domain.mapper.TranslationCacheMapper;
import com.abswitch.weblog.common.service.translation.TranslationService;
import com.abswitch.weblog.common.utils.BaiduTranslateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class TranslationServiceImpl implements TranslationService {

    /** 百度翻译单次请求最大字符数（保守值，文档限制 6000） */
    private static final int MAX_CHARS_PER_REQUEST = 5000;

    /** 1 QPS 限速间隔，含余量 */
    private static final long QPS_INTERVAL_MS = 1100L;

    @Autowired
    private TranslationCacheMapper translationCacheMapper;

    @Autowired
    private BaiduTranslateProperties baiduTranslateProperties;

    @Override
    public String translateAndCache(String sourceText, String sourceLang, String targetLang) {
        if (sourceText == null || sourceText.isBlank()) {
            return sourceText;
        }
        if (sourceLang.equals(targetLang)) {
            return sourceText;
        }

        String hash = sha256(sourceText);
        TranslationCacheDO cached = translationCacheMapper.selectByHash(hash, sourceLang, targetLang);
        if (cached != null) {
            return cached.getTranslatedText();
        }

        String translated;
        try {
            if (sourceText.length() < MAX_CHARS_PER_REQUEST) {
                translated = BaiduTranslateUtil.translate(
                        sourceText, sourceLang, targetLang,
                        baiduTranslateProperties.getAppId(),
                        baiduTranslateProperties.getSecretKey()
                );
            } else {
                translated = translateLongText(sourceText, sourceLang, targetLang);
            }
        } catch (Exception e) {
            log.error("百度翻译API异常: {}", e.getMessage());
            return null;
        }

        if (translated == null) {
            return null;
        }

        try {
            TranslationCacheDO entity = TranslationCacheDO.builder()
                    .sourceHash(hash)
                    .sourceLang(sourceLang)
                    .targetLang(targetLang)
                    .sourceText(sourceText)
                    .translatedText(translated)
                    .createTime(LocalDateTime.now())
                    .build();
            translationCacheMapper.insert(entity);
        } catch (DuplicateKeyException dup) {
            // 并发场景：另一线程已写入，忽略
        } catch (Exception e) {
            log.warn("[Translation] 缓存写入失败: {}", e.getMessage());
        }
        return translated;
    }

    @Override
    public String getCachedOrNull(String sourceText, String sourceLang, String targetLang) {
        if (sourceText == null || sourceText.isBlank() || sourceLang.equals(targetLang)) {
            return sourceText;
        }
        TranslationCacheDO cached = translationCacheMapper.selectByHash(
                sha256(sourceText), sourceLang, targetLang);
        return cached != null ? cached.getTranslatedText() : null;
    }

    @Override
    public String getSourceByTranslated(String translatedText, String sourceLang, String targetLang) {
        if (translatedText == null || translatedText.isBlank() || sourceLang.equals(targetLang)) {
            return translatedText;
        }
        TranslationCacheDO row = translationCacheMapper.selectByTranslated(translatedText, sourceLang, targetLang);
        return row != null ? row.getSourceText() : null;
    }

    @Override
    public Map<String, String> getTranslations(List<String> sourceTexts, String sourceLang, String targetLang) {
        if (sourceTexts == null || sourceTexts.isEmpty() || sourceLang.equals(targetLang)) {
            return Collections.emptyMap();
        }
        Map<String, String> hashToSource = new HashMap<>();
        for (String src : sourceTexts) {
            if (src == null || src.isBlank()) continue;
            hashToSource.put(sha256(src), src);
        }
        if (hashToSource.isEmpty()) {
            return Collections.emptyMap();
        }
        List<TranslationCacheDO> rows = translationCacheMapper.selectByHashes(
                new ArrayList<>(hashToSource.keySet()), sourceLang, targetLang);
        Map<String, String> result = new HashMap<>();
        for (TranslationCacheDO row : rows) {
            String src = hashToSource.get(row.getSourceHash());
            if (src != null) {
                result.put(src, row.getTranslatedText());
            }
        }
        return result;
    }

    /**
     * 长文本分段翻译。按 \n\n 切段，遇到代码块（``` 围栏）整段保留不翻译，
     * 累积段长度逼近上限时拼接发送，每次请求间隔 1.1s。
     */
    private String translateLongText(String sourceText, String sourceLang, String targetLang) {
        String[] paragraphs = sourceText.split("\n\n", -1);
        StringBuilder out = new StringBuilder();
        StringBuilder buffer = new StringBuilder();
        boolean inCodeBlock = false;
        boolean firstChunk = true;

        for (int i = 0; i < paragraphs.length; i++) {
            String p = paragraphs[i];
            boolean codeFenceToggle = false;
            int fenceCount = countOccurrences(p, "```");
            if (fenceCount % 2 == 1) {
                codeFenceToggle = true;
            }

            if (inCodeBlock || (p.startsWith("```") && fenceCount > 0)) {
                // 遇到代码块：先 flush 缓冲区
                if (!buffer.isEmpty()) {
                    out.append(translateChunk(buffer.toString(), sourceLang, targetLang, firstChunk));
                    firstChunk = false;
                    buffer.setLength(0);
                }
                if (!out.isEmpty()) out.append("\n\n");
                out.append(p);
                if (codeFenceToggle) inCodeBlock = !inCodeBlock;
                continue;
            }

            String candidate = buffer.isEmpty() ? p : buffer + "\n\n" + p;
            if (candidate.length() > MAX_CHARS_PER_REQUEST && !buffer.isEmpty()) {
                out.append(translateChunk(buffer.toString(), sourceLang, targetLang, firstChunk));
                firstChunk = false;
                buffer.setLength(0);
                buffer.append(p);
            } else {
                buffer.setLength(0);
                buffer.append(candidate);
            }
        }
        if (!buffer.isEmpty()) {
            out.append(translateChunk(buffer.toString(), sourceLang, targetLang, firstChunk));
        }
        return out.toString();
    }

    private String translateChunk(String chunk, String sourceLang, String targetLang, boolean isFirst) {
        if (!isFirst) {
            try {
                Thread.sleep(QPS_INTERVAL_MS);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
        }
        String t = BaiduTranslateUtil.translate(chunk, sourceLang, targetLang,
                baiduTranslateProperties.getAppId(),
                baiduTranslateProperties.getSecretKey());
        StringBuilder sb = new StringBuilder();
        if (!isFirst) sb.append("\n\n");
        sb.append(t);
        return sb.toString();
    }

    private static int countOccurrences(String text, String token) {
        int count = 0;
        int idx = 0;
        while ((idx = text.indexOf(token, idx)) != -1) {
            count++;
            idx += token.length();
        }
        return count;
    }

    private static String sha256(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(input.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 计算失败", e);
        }
    }
}
