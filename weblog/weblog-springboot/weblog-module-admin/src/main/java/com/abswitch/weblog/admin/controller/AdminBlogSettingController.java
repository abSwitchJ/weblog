package com.abswitch.weblog.admin.controller;

import com.abswitch.weblog.admin.model.vo.blogsettings.UpdateBlogSettingsReqVO;
import com.abswitch.weblog.admin.service.AdminBlogSettingsService;
import com.abswitch.weblog.common.aspect.ApiOperationLog;
import com.abswitch.weblog.common.utils.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author：abSwitch
 * @url：
 * @date：2026-03-23 19:25
 * @Description：
 */
@RestController
@RequestMapping("admin/blog/settings")
@Tag(name = "Admin 博客设置模块")
public class AdminBlogSettingController {

    @Autowired
    private AdminBlogSettingsService adminBlogSettingsService;

    @PostMapping("/update")
    @Operation(summary = "博客基础信息修改")
    @ApiOperationLog(description = "博客基础信息修改")
    public Response updateBlogSettings(@RequestBody @Validated UpdateBlogSettingsReqVO updateBlogSettingReqVO){

        return adminBlogSettingsService.updateBlogSettings(updateBlogSettingReqVO);
    }

    @PostMapping("/detial")
    @Operation(summary = "获取博客设置详情")
    @ApiOperationLog(description = "获取博客设置详情")
    public Response findDetail(){

        return adminBlogSettingsService.findDetail();
    }
}
