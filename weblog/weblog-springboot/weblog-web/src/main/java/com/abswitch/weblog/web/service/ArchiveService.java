package com.abswitch.weblog.web.service;

import com.abswitch.weblog.common.utils.Response;
import com.abswitch.weblog.web.model.vo.FindIndexArticleOrArchivePageListReqVO;

/**
 * @Author：abSwitch
 * @url：
 * @date：2026-03-26 13:56
 * @Description：
 */
public interface ArchiveService {
    Response findArchivePageList(FindIndexArticleOrArchivePageListReqVO findArchiveArticlePageListReqVO);
}
