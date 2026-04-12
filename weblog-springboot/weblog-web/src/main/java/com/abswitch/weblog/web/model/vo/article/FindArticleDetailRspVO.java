package com.abswitch.weblog.web.model.vo.article;

import com.abswitch.weblog.web.model.vo.FindCategoryOrTagListRspVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author：abSwitch
 * @url：
 * @date：2026-03-26 17:58
 * @Description：
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FindArticleDetailRspVO {

    /**
     * 文章标题
     */
    private String title;

    /**
     * 文章正文
     */
    private String content;
    /**
     * 发布时间
     */
    private LocalDateTime createTime;
    /**
     * 所属分类id
     */
    private Long categoryId;

    /**
     * 文章分类
     */
    private String categoryName;
    /**
     * 阅读量
     */
    private Long readNum;

    /**
     * 文章标签
     */
    private List<FindCategoryOrTagListRspVO> tags;

    /**
     * 上一篇文章
     */
    private ArticleRspVO preArticle;
    /**
     * 下一篇文章
     */
    private ArticleRspVO nextArticle;
    /**
     * 总字数
     */
    private Integer totalWords;

    /**
     * 阅读时长
     */
    private String readTime;
}
