package com.abswitch.weblog.web.model.vo;

import com.abswitch.weblog.common.model.BasePageQuery;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class FindTagArticleByNameReqVO extends BasePageQuery {

    @NotBlank(message = "标签名称不能为空")
    private String name;
}
