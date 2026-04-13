package com.abswitch.weblog.admin.model.vo.file;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;

/**
 * @Author：abSwitch
 * @url：
 * @date：2026-03-23 18:36
 * @Description：
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "上传文件 VO")
public class UploadFileRspVO {
    /**
     * 文件的访问链接
     */
    private String url;
}
