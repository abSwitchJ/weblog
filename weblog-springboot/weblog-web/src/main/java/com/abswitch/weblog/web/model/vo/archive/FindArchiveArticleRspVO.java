package com.abswitch.weblog.web.model.vo.archive;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * @Author：abSwitch
 * @url：
 * @date：2026-03-26 13:54
 * @Description：
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FindArchiveArticleRspVO {
    private Long id;
    private String title;
    private String slug;
    /**
     * 发布日期
     */
    private LocalDate createDate;
}

