package com.abswitch.weblog.web.controller;

import com.abswitch.weblog.common.aspect.ApiOperationLog;
import com.abswitch.weblog.common.utils.Response;
import com.abswitch.weblog.web.model.vo.FindCategoryOrTagOrArticlePageListReqVO;
import com.abswitch.weblog.web.model.vo.FindCategoryOrTagListReqVO;
import com.abswitch.weblog.web.model.vo.FindTagArticleByNameReqVO;
import com.abswitch.weblog.web.model.vo.FindTagNameReqVO;
import com.abswitch.weblog.web.service.TagService;
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
 * @date：2026-03-26 10:58
 * @Description：
 */
@RestController
@RequestMapping("/tag")
@Tag(name = "标签")
public class TagController {

    @Autowired
    private TagService tagService;

    @PostMapping("/list")
    @ApiOperationLog(description = "前台获取标签列表")
    @Operation(summary = "前台获取标签列表")
    public Response findTagList(@RequestBody @Validated FindCategoryOrTagListReqVO findCategoryOrTagListReqVO){

        return tagService.findTagList(findCategoryOrTagListReqVO);
    }

    @PostMapping("/article/list")
    @ApiOperationLog(description = "前台获取标签下文章分页数据")
    @Operation(summary = "前台获取标签下文章分页数据")
    public Response findTagArticlePageList(@RequestBody @Validated FindCategoryOrTagOrArticlePageListReqVO findTagArticlePageListReqVO) {
        return tagService.findTagArticlePageList(findTagArticlePageListReqVO);
    }

    @PostMapping("/article/list/by-name")
    @ApiOperationLog(description = "前台按标签名获取文章分页数据")
    @Operation(summary = "前台按标签名获取文章分页数据")
    public Response findTagArticlePageListByName(@RequestBody @Validated FindTagArticleByNameReqVO reqVO) {
        return tagService.findTagArticlePageListByName(reqVO);
    }

    @PostMapping("/name/resolve")
    @ApiOperationLog(description = "前台按标签名解析中英文名称对")
    @Operation(summary = "前台按标签名解析中英文名称对")
    public Response resolveTagName(@RequestBody @Validated FindTagNameReqVO reqVO) {
        return tagService.resolveTagName(reqVO);
    }
}
