package com.abswitch.weblog.web.model.vo.article;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FindArticleDetailBySlugReqVO {

    @NotBlank(message = "文章 slug 不能为空")
    private String slug;
}
