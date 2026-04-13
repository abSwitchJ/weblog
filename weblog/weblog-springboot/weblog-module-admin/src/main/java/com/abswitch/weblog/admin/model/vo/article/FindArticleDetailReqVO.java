package com.abswitch.weblog.admin.model.vo.article;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
@Schema(description = "获取文章详情入参 VO")
public class FindArticleDetailReqVO {

    @NotNull(message = "文章 id 不能为空")
    private Long id;
}
