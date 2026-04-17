package com.abswitch.weblog.common.domain.dos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("t_translation_cache")
public class TranslationCacheDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String sourceHash;

    private String sourceLang;

    private String targetLang;

    private String sourceText;

    private String translatedText;

    private LocalDateTime createTime;
}
