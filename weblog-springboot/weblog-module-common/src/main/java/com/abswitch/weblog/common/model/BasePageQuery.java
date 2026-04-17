package com.abswitch.weblog.common.model;

import lombok.Data;

/**
 * @Author：abSwitch
 * @url：
 * @date：2026-03-21 20:36
 * @Description：
 */
@Data
public class BasePageQuery {

    /**
     * 当前页码，默认第一页
     */
    private Long current = 1L;

    /**
     * 每页展示的数据数量，默认每页展示10条数据
     */
    private Long size = 10L;

    /**
     * 语言（前台中英文切换）：zh/en，默认 zh
     */
    private String lang = "zh";
}
