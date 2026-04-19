package com.abswitch.weblog.admin.model.vo.tag;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "更新标签 VO")
public class UpdateTagReqVO {

    @NotNull(message = "标签 ID 不能为空")
    private Long id;

    @NotBlank(message = "标签名称不能为空")
    @Length(min = 1, max = 20, message = "标签名称字数限制 1 ~ 20 之间")
    private String name;
}
