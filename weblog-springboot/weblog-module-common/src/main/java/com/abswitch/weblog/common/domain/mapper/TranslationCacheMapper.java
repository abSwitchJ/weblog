package com.abswitch.weblog.common.domain.mapper;

import com.abswitch.weblog.common.domain.dos.TranslationCacheDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;

import java.util.List;

public interface TranslationCacheMapper extends BaseMapper<TranslationCacheDO> {

    default TranslationCacheDO selectByHash(String sourceHash, String sourceLang, String targetLang) {
        return selectOne(Wrappers.<TranslationCacheDO>lambdaQuery()
                .eq(TranslationCacheDO::getSourceHash, sourceHash)
                .eq(TranslationCacheDO::getSourceLang, sourceLang)
                .eq(TranslationCacheDO::getTargetLang, targetLang)
                .last("LIMIT 1"));
    }

    default List<TranslationCacheDO> selectByHashes(List<String> sourceHashes, String sourceLang, String targetLang) {
        if (sourceHashes == null || sourceHashes.isEmpty()) {
            return List.of();
        }
        return selectList(Wrappers.<TranslationCacheDO>lambdaQuery()
                .in(TranslationCacheDO::getSourceHash, sourceHashes)
                .eq(TranslationCacheDO::getSourceLang, sourceLang)
                .eq(TranslationCacheDO::getTargetLang, targetLang));
    }

    default TranslationCacheDO selectByTranslated(String translatedText, String sourceLang, String targetLang) {
        return selectOne(Wrappers.<TranslationCacheDO>lambdaQuery()
                .eq(TranslationCacheDO::getTranslatedText, translatedText)
                .eq(TranslationCacheDO::getSourceLang, sourceLang)
                .eq(TranslationCacheDO::getTargetLang, targetLang)
                .last("LIMIT 1"));
    }
}
