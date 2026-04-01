package com.abswitch.weblog.admin.service;

import com.abswitch.weblog.admin.model.vo.article.*;
import com.abswitch.weblog.common.utils.Response;

/**
 * @Author：abSwitch
 * @url：
 * @date：2026-03-23 21:08
 * @Description：
 */
public interface AdminArticleService {
    Response publishArticle(PublishArticleReqVO publishArticleReqVO);

    Response deleteArticle(DeleteArticleReqVO deleteArticleReqVO);

    Response findArticlePageList(FindArticlePageListReqVO findArticlePageListReqVO);

    Response findArticleDetail(FindArticleDetailReqVO findArticleDetailReqVO);

    Response updateArticleReqVO(UpdateArticleReqVO updateArticleReqVO);

}
