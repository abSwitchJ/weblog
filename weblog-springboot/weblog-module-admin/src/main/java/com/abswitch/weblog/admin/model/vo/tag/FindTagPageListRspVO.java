package com.abswitch.weblog.admin.model.vo.tag;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @Author：abSwitch
 * @url：
 * @date：2026-03-22 15:21
 * @Description：
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "查询标签分页数据出参 VO")
public class FindTagPageListRspVO {

    /**
     * 标签 ID
     */
    private Long id;

    /**
     * 标签名称
     */
    private String name;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;


    /**
     * 文章总数
     */
    private Integer articlesTotal;
}
