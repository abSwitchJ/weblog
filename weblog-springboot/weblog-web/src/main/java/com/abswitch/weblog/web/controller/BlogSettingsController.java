package com.abswitch.weblog.web.controller;

import com.abswitch.weblog.common.aspect.ApiOperationLog;
import com.abswitch.weblog.common.utils.Response;
import com.abswitch.weblog.web.service.BlogSettingsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author：abSwitch
 * @url：
 * @date：2026-03-26 11:16
 * @Description：
 */
@RestController
@RequestMapping("/blog/settings")
@Tag(name = "博客设置")
public class BlogSettingsController {

    @Autowired
    private BlogSettingsService blogSettingsService;

    @PostMapping("/detail")
    @ApiOperationLog(description = "前台获取博客详情")
    @Operation(summary = "前台获取博客详情")
    public Response findDetail(){

        return blogSettingsService.findDetail();
    }
}
