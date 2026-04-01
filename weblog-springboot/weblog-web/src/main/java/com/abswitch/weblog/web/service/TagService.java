package com.abswitch.weblog.web.service;

import com.abswitch.weblog.common.utils.Response;
import com.abswitch.weblog.web.model.vo.category.FindCategoryListReqVO;
import com.abswitch.weblog.web.model.vo.tag.FindTagArticlePageListReqVO;
import com.abswitch.weblog.web.model.vo.tag.FindTagListReqVO;

/**
 * @Author：abSwitch
 * @url：
 * @date：2026-03-26 11:00
 * @Description：
 */
public interface TagService {
    Response findTagList(FindTagListReqVO findTagListReqVO);

    Response findTagArticlePageList(FindTagArticlePageListReqVO findTagArticlePageListReqVO);
}
