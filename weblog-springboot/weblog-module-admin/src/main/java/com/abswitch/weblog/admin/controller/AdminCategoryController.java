package com.abswitch.weblog.admin.controller;

import com.abswitch.weblog.admin.model.vo.category.AddCategoryReqVO;
import com.abswitch.weblog.admin.model.vo.category.DeleteCategoryReqVO;
import com.abswitch.weblog.admin.model.vo.category.FindCategoryPageListReqVO;
import com.abswitch.weblog.admin.model.vo.category.UpdateCategoryReqVO;
import com.abswitch.weblog.admin.service.AdminCategoryService;
import com.abswitch.weblog.common.aspect.ApiOperationLog;
import com.abswitch.weblog.common.utils.PageResponse;
import com.abswitch.weblog.common.utils.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @Author：abSwitch
 * @url：
 * @date：2026-03-21 18:42
 * @Description：
 */
@RestController
@RequestMapping("/admin")
@Tag(name = "Admin 分类模块")
public class AdminCategoryController {

    @Autowired
    private AdminCategoryService adminCategoryService;

    @PostMapping("/category/add")
    @ApiOperationLog(description = "新增分类")
    @Operation(summary = "新增分类")
    public Response addCategory(@RequestBody @Validated AddCategoryReqVO addCategoryReqVO){

        return adminCategoryService.addCategory(addCategoryReqVO);
    }

    @PostMapping("/category/list")
    @ApiOperationLog(description = "分页查询")
    @Operation(summary = "分页查询")
    public PageResponse findCategoryList(@RequestBody @Validated FindCategoryPageListReqVO findCategoryPageListReqVO){

        return adminCategoryService.findCategoryList(findCategoryPageListReqVO);
    }

    @PostMapping("/category/delete")
    @ApiOperationLog(description = "删除分类")
    @Operation(summary = "删除分类")
    public Response deleteCategory(@RequestBody @Validated DeleteCategoryReqVO deleteCategoryReqVO){

        return adminCategoryService.deleteCategory(deleteCategoryReqVO);
    }

    @PostMapping("/category/update")
    @ApiOperationLog(description = "更新分类")
    @Operation(summary = "更新分类")
    public Response updateCategory(@RequestBody @Validated UpdateCategoryReqVO updateCategoryReqVO){

        return adminCategoryService.updateCategory(updateCategoryReqVO);
    }

    @PostMapping("/category/select/list")
    @ApiOperationLog(description = "分类 Select 下拉列表数据获取")
    @Operation(summary = "分类 Select 下拉列表数据获取")
    public Response findCategorySelectList(){
        return adminCategoryService.findCategorySelectList();
    }
}
