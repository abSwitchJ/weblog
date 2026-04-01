package com.abswitch.weblog.web.model.vo.archive;

import com.abswitch.weblog.common.model.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

/**
 * @Author：abSwitch
 * @url：
 * @date：2026-03-26 13:45
 * @Description：
 */
@Data
@Builder
@Schema(description = "文章归档分页 VO")
public class FindArchiveArticlePageListReqVO extends BasePageQuery {
}

