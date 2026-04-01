package com.abswitch.weblog.admin.model.vo.category;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

/**
 * @Author：abSwitch
 * @url：
 * @date：2026-03-21 17:35
 * @Description：
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "添加分类 VO")
public class AddCategoryReqVO {

    @NotBlank(message = "分类名称不能为空")
    @Length(min = 1, max = 10, message = "分类名称字数限制 1 ~ 10 之间")
    private String name;
}
