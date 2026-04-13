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
 * @date：2026-04-05 12:25
 * @Description：
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "置顶文章入参 VO")
public class IsTopUpdateArticleReqVO {
    @NotNull(message = "文章 ID 不能为空")
    private Long id;
    @NotNull(message = "文章置顶状态不能为空")
    private Boolean isTop;
}
