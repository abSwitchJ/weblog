package com.abswitch.weblog.web.controller;

import com.abswitch.weblog.common.aspect.ApiOperationLog;
import com.abswitch.weblog.common.utils.Response;
import com.abswitch.weblog.web.service.StatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author：abSwitch
 * @url：
 * @date：2026-03-30 12:59
 * @Description：
 */
@RestController
@RequestMapping("/statistics")
@Tag(name = "统计信息")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    @PostMapping("/info")
    @ApiOperationLog(description = "前台获取统计信息")
    @Operation(summary = "前台获取统计信息")
    public Response findArticlePageList() {
        return statisticsService.findArticlePageList();
    }
}
