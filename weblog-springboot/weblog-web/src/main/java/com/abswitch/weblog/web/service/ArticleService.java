package com.abswitch.weblog.web.service;

import com.abswitch.weblog.common.utils.Response;
import com.abswitch.weblog.web.model.vo.FindCategoryOrTagOrArticlePageListReqVO;
import com.abswitch.weblog.web.model.vo.FindIndexArticleOrArchivePageListReqVO;

/**
 * @Author：abSwitch
 * @url：
 * @date：2026-03-25 14:01
 * @Description：
 */
public interface ArticleService {
    Response findArticlePageList(FindIndexArticleOrArchivePageListReqVO findIndexArticlePageListReqVO);

    Response findArticleDetail(FindCategoryOrTagOrArticlePageListReqVO findArticleDetailReqVO);

}
