package com.abswitch.weblog.web.model.vo.category;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author：abSwitch
 * @url：
 * @date：2026-03-25 14:09
 * @Description：
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FindCategoryListRspVO {

    private Long id;
    private String name;
    private Integer articlesTotal;

}
