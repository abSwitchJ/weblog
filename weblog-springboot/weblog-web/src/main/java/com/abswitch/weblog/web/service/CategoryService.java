package com.abswitch.weblog.web.service;

import com.abswitch.weblog.common.utils.Response;
import com.abswitch.weblog.web.model.vo.category.FindCategoryArticlePageListReqVO;
import com.abswitch.weblog.web.model.vo.category.FindCategoryListReqVO;

/**
 * @Author：abSwitch
 * @url：
 * @date：2026-03-26 10:24
 * @Description：
 */
public interface CategoryService {
    Response findCategoryList(FindCategoryListReqVO findCategoryListReqVO);

    Response findCategoryArticlePageList(FindCategoryArticlePageListReqVO findCategoryArticlePageListReqVO);

}
