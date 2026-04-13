package com.abswitch.weblog.admin.controller;

/**
 * @Author：abSwitch
 * @url：
 * @date：2026-03-23 18:41
 * @Description：
 */

import com.abswitch.weblog.admin.service.AdminFileService;
import com.abswitch.weblog.common.aspect.ApiOperationLog;
import com.abswitch.weblog.common.utils.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/admin")
@Tag(name = "Admin 文件模块")
public class AdminFileController {

    @Autowired
    private AdminFileService adminFileService;

    @PostMapping("/file/upload")
    @ApiOperationLog(description = "文件上传")
    @Operation(summary = "文件上传")
    public Response uploadFile(@RequestParam MultipartFile file){

        return adminFileService.uploadFile(file);
    }

}
