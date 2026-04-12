package com.abswitch.weblog.web.model.vo;

import com.abswitch.weblog.common.model.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Author：abSwitch
 * @url：
 * @date：2026-03-25 14:03
 * @Description：
 */
@Data
@Schema(description = "首页查询文章分页 VO")
public class FindIndexArticleOrArchivePageListReqVO extends BasePageQuery {

}
