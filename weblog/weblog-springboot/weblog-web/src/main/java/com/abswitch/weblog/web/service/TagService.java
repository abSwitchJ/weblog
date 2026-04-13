package com.abswitch.weblog.web.service;

import com.abswitch.weblog.common.utils.Response;
import com.abswitch.weblog.web.model.vo.FindCategoryOrTagOrArticlePageListReqVO;
import com.abswitch.weblog.web.model.vo.FindCategoryOrTagListReqVO;

/**
 * @Author：abSwitch
 * @url：
 * @date：2026-03-26 11:00
 * @Description：
 */
public interface TagService {
    Response findTagList(FindCategoryOrTagListReqVO findCategoryOrTagListReqVO);

    Response findTagArticlePageList(FindCategoryOrTagOrArticlePageListReqVO findTagArticlePageListReqVO);
}
