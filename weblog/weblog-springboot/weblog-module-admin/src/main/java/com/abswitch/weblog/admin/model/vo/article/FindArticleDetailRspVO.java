package com.abswitch.weblog.admin.model.vo.article;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author：abSwitch
 * @url：
 * @date：2026-03-24 19:42
 * @Description：
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "获取文章详情出参 VO")
public class FindArticleDetailRspVO {

    private Long id;

    private String title;
    private String cover;
    private String content;
    private String summary;
    private Long categoryId;
    private List<Long> tagIds;


}
