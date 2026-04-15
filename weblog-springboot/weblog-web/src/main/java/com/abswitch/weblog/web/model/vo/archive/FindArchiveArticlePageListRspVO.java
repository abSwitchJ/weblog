package com.abswitch.weblog.web.model.vo.archive;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author：abSwitch
 * @url：
 * @date：2026-03-26 13:45
 * @Description：
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FindArchiveArticlePageListRspVO {

    private Integer year;

    private List<FindArchiveArticleRspVO> articles;
}
