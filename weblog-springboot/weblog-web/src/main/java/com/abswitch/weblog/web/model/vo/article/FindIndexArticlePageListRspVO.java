package com.abswitch.weblog.web.model.vo.article;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * @Author：abSwitch
 * @url：
 * @date：2026-03-25 14:03
 * @Description：
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FindIndexArticlePageListRspVO {

    /**
     * 文章 ID
     */
    private Long id;

    /**
     * 文章标题
     */
    private String title;

    /**
     * 文章 slug
     */
    private String slug;

    /**
     * 发布时间
     */
    private LocalDate createDate;

    /**
     * 文章摘要
     */
    private String summary;

}
