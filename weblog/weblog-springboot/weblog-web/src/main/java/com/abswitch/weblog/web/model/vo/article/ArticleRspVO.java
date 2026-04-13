package com.abswitch.weblog.web.model.vo.article;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author：abSwitch
 * @url：
 * @date：2026-03-26 18:08
 * @Description：
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ArticleRspVO {

    private Long articleId;
    private String articleTitle;
}
