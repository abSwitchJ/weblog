package com.abswitch.weblog.admin.model.vo.tag;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author：abSwitch
 * @url：
 * @date：2026-03-22 15:40
 * @Description：
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "删除标签 VO")
public class DeleteTagReqVO {

    @NotNull(message = "标签 ID 不能为空")
    private Long id;

}
