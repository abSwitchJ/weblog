package com.abswitch.weblog.web.model.vo.tag;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author：abSwitch
 * @url：
 * @date：2026-03-25 14:10
 * @Description：
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FindTagListRspVO {

    private Long id;
    private String name;
    private Integer articlesTotal;
}
