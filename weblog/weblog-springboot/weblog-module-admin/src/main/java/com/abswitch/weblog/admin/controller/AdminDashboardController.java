package com.abswitch.weblog.admin.controller;

import com.abswitch.weblog.admin.service.AdminDashboardService;
import com.abswitch.weblog.common.aspect.ApiOperationLog;
import com.abswitch.weblog.common.utils.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @Author：abSwitch
 * @url：
 * @date：2026-03-26 20:51
 * @Description：
 */
@RestController
@RequestMapping("/admin/dashboard")
@Tag(name = "Admin 仪表盘")
public class AdminDashboardController {

    @Autowired
    private AdminDashboardService adminDashboardService;

    @PostMapping("/statistics")
    @ApiOperationLog(description = "获取后台仪表盘基础统计信息")
    @Operation(summary = "获取后台仪表盘基础统计信息")
    public Response findDashboardStatistics() {
        return adminDashboardService.findDashboardStatistics();
    }

    @PostMapping("/publishArticle/statistics")
    @ApiOperationLog(description = "获取后台仪表盘文章发布热点统计信息")
    @Operation(summary = "获取后台仪表盘文章发布热点统计信息")
    public Response findPublishArticleStatistics(){
        return adminDashboardService.findPublishArticleStatistics();
    }

    @PostMapping("/pv/statistics")
    @ApiOperationLog(description = "获取后台仪表盘最近一周 PV 访问量信息")
    @Operation(summary = "获取后台仪表盘最近一周 PV 访问量信息")
    public Response findDashboardPVStatistics(){
        return adminDashboardService.findDashboardPVStatistics();
    }
}
