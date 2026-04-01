package com.abswitch.weblog.admin.model.vo.tag;

import com.abswitch.weblog.common.model.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * @Author：abSwitch
 * @url：
 * @date：2026-03-22 14:49
 * @Description：
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "查询标签分页数据入参 VO")
public class FindTagPageListReqVO extends BasePageQuery {

    private String name;

    private LocalDate startTime;

    private LocalDate endTime;
}
