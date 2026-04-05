package com.abswitch.weblog.web.model.vo.article;

import com.abswitch.weblog.web.model.vo.category.FindCategoryListRspVO;
import com.abswitch.weblog.web.model.vo.tag.FindTagListRspVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

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
     * 文章封面
     */
    private String cover;

    /**
     * 文章标题
     */
    private String title;

    /**
     * 发布时间
     */
    private LocalDate createDate;

    /**
     * 文章摘要
     */
    private String summary;

    /**
     * 文章标签
     */
    private List<FindTagListRspVO> tags;

    /**
     * 文章分类
     */
    private FindCategoryListRspVO category;

    /**
     * 文章置顶
     */
    private Boolean isTop;

}
