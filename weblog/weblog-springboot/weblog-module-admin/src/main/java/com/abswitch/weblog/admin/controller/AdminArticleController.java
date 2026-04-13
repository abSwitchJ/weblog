package com.abswitch.weblog.admin.controller;

import com.abswitch.weblog.admin.model.vo.article.*;
import com.abswitch.weblog.admin.service.AdminArticleService;
import com.abswitch.weblog.common.aspect.ApiOperationLog;
import com.abswitch.weblog.common.utils.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author：abSwitch
 * @url：
 * @date：2026-03-23 21:06
 * @Description：
 */
@RestController
@RequestMapping("/admin/article")
@Tag(name = "Admin 文章模块")
public class AdminArticleController {

    @Autowired
    private AdminArticleService adminArticleService;

    @PostMapping("/publish")
    @ApiOperationLog(description = "文章发布")
    @Operation(summary = "文章发布")
    //SpEL（Spring Expression Language）表达式，判断当前认证用户是否拥有 ROLE_ADMIN 角色。
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Response publishArticle(@RequestBody @Validated PublishArticleReqVO publishArticleReqVO){

        return adminArticleService.publishArticle(publishArticleReqVO);
    }

    @PostMapping("/delete")
    @ApiOperationLog(description = "文章删除")
    @Operation(summary = "文章删除")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Response deleteArticle(@RequestBody @Validated DeleteArticleReqVO deleteArticleReqVO){

        return adminArticleService.deleteArticle(deleteArticleReqVO);
    }

    @PostMapping("/list")
    @ApiOperationLog(description = "查询文章分页数据")
    @Operation(summary = "查询文章分页数据")
    public Response findArticlePageList(@RequestBody @Validated FindArticlePageListReqVO findArticlePageListReqVO){

        return adminArticleService.findArticlePageList(findArticlePageListReqVO);
    }

    @PostMapping("/detail")
    @ApiOperationLog(description = "获取文章详情")
    @Operation(summary = "获取文章详情")
    public Response findArticleDetail(@RequestBody @Validated FindArticleDetailReqVO findArticleDetailReqVO){

        return adminArticleService.findArticleDetail(findArticleDetailReqVO);
    }

    @PostMapping("/update")
    @ApiOperationLog(description = "文章更新")
    @Operation(summary = "文章更新")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Response updateArticle(@RequestBody @Validated UpdateArticleReqVO updateArticleReqVO){

        return adminArticleService.updateArticle(updateArticleReqVO);
    }

    @PostMapping("/isTop/update")
    @ApiOperationLog(description = "文章置顶")
    @Operation(summary = "文章置顶")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Response isTopUpdateArticle(@RequestBody @Validated IsTopUpdateArticleReqVO isTopUpdateArticleReqVO){

        return adminArticleService.isTopUpdateArticle(isTopUpdateArticleReqVO);
    }
}
