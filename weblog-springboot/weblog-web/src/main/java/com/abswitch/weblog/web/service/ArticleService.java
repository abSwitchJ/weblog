package com.abswitch.weblog.web.service;

import com.abswitch.weblog.common.utils.Response;
import com.abswitch.weblog.web.model.vo.article.FindArticleDetailReqVO;
import com.abswitch.weblog.web.model.vo.article.FindIndexArticlePageListReqVO;

/**
 * @Author：abSwitch
 * @url：
 * @date：2026-03-25 14:01
 * @Description：
 */
public interface ArticleService {
    Response findArticlePageList(FindIndexArticlePageListReqVO findIndexArticlePageListReqVO);

    Response findArticleDetail(FindArticleDetailReqVO findArticleDetailReqVO);

}
