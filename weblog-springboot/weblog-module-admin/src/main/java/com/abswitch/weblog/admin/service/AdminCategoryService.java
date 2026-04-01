package com.abswitch.weblog.admin.service;

import com.abswitch.weblog.admin.model.vo.category.AddCategoryReqVO;
import com.abswitch.weblog.admin.model.vo.category.DeleteCategoryReqVO;
import com.abswitch.weblog.admin.model.vo.category.FindCategoryPageListReqVO;
import com.abswitch.weblog.common.utils.PageResponse;
import com.abswitch.weblog.common.utils.Response;

import java.util.List;

/**
 * @Author：abSwitch
 * @url：
 * @date：2026-03-21 17:43
 * @Description：
 */
public interface AdminCategoryService {

    Response addCategory(AddCategoryReqVO addCategoryReqVO);

    PageResponse findCategoryList(FindCategoryPageListReqVO findCategoryPageListReqVO);

    Response deleteCategory(DeleteCategoryReqVO deleteCategoryReqVO);

    Response findCategorySelectList();
}
