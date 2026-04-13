package com.abswitch.weblog.admin.controller;

import com.abswitch.weblog.admin.model.vo.tag.AddTagsReqVO;
import com.abswitch.weblog.admin.model.vo.tag.DeleteTagReqVO;
import com.abswitch.weblog.admin.model.vo.tag.FindTagPageListReqVO;
import com.abswitch.weblog.admin.service.AdminTagService;
import com.abswitch.weblog.common.aspect.ApiOperationLog;
import com.abswitch.weblog.common.utils.PageResponse;
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
 * @date：2026-03-22 14:15
 * @Description：
 */
@RestController
@RequestMapping("/admin")
@Tag(name = "Admin 标签模块")
public class AdminTagController {

    @Autowired
    private AdminTagService adminTagService;

    @PostMapping("/tag/add")
    @ApiOperationLog(description = "新增标签")
    @Operation(summary = "新增标签")
    public Response addTag(@RequestBody @Validated AddTagsReqVO addTagsReqVO){

        return adminTagService.addTags(addTagsReqVO);
    }

    @PostMapping("/tag/list")
    @ApiOperationLog(description = "分页查询")
    @Operation(summary = "分页查询")
    public PageResponse findTagList(@RequestBody @Validated FindTagPageListReqVO findTagPageListReqVO){

        return adminTagService.findTagList(findTagPageListReqVO);
    }

    @PostMapping("/tag/delete")
    @ApiOperationLog(description = "删除标签")
    @Operation(summary = "删除标签")
    public Response deleteTag(@RequestBody @Validated DeleteTagReqVO deleteTagReqVO){

        return adminTagService.deleteTag(deleteTagReqVO);
    }

    @PostMapping("/tag/select/list")
    @ApiOperationLog(description = "标签 Select 下拉列表数据获取")
    @Operation(summary = "标签 Select 下拉列表数据获取")
    public Response FindTagSelectList(){

        return adminTagService.findTagSelectList();
    }
}
