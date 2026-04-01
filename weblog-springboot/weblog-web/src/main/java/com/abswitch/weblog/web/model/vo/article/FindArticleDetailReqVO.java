package com.abswitch.weblog.web.model.vo.article;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
public class FindArticleDetailReqVO {

    @NotNull(message = "文章 Id 不能为空")
    private Long id;
}
