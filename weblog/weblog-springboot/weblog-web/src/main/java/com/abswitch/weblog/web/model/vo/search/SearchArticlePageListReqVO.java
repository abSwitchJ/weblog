package com.abswitch.weblog.web.model.vo.search;

import com.abswitch.weblog.common.model.BasePageQuery;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * @Author：abSwitch
 * @url：
 * @date：2026-03-30 16:55
 * @Description：
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SearchArticlePageListReqVO extends BasePageQuery {
    /**
     * 查询关键词
     */
    @NotBlank(message = "搜索关键词不能为空")
    private String word;
}

