package com.abswitch.weblog.web.model.vo.archive;

import com.abswitch.weblog.common.model.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.YearMonth;
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

    private YearMonth month;

    private List<FindArchiveArticleRspVO> articles;
}
