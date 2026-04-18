package com.abswitch.weblog.web.service;

import com.abswitch.weblog.common.utils.Response;
import com.abswitch.weblog.web.model.vo.blogsettings.FindAboutDetailReqVO;

/**
 * @Author：abSwitch
 * @url：
 * @date：2026-03-26 11:22
 * @Description：
 */
public interface BlogSettingsService {
    Response findDetail();

    Response findAboutDetail(FindAboutDetailReqVO reqVO);
}
