package com.abswitch.weblog.web.service;

import com.abswitch.weblog.common.utils.Response;
import com.abswitch.weblog.web.model.vo.search.SearchArticlePageListReqVO;

/**
 * @Author：abSwitch
 * @url：
 * @date：2026-03-30 17:00
 * @Description：
 */
public interface SearchService {
    Response searchArticlePageList(SearchArticlePageListReqVO searchArticlePageListReqVO);
}
