package com.abswitch.weblog.web.model.vo.tag;

import com.abswitch.weblog.common.model.BasePageQuery;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author：abSwitch
 * @url：
 * @date：2026-03-26 16:17
 * @Description：
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FindTagArticlePageListReqVO extends BasePageQuery {

    @NotNull(message = "标签 Id 不能为空")
    private Long id;
}
