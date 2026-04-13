package com.abswitch.weblog.admin.service;

import com.abswitch.weblog.admin.model.vo.blogsettings.UpdateBlogSettingsReqVO;
import com.abswitch.weblog.common.utils.Response;

/**
 * @Author：abSwitch
 * @url：
 * @date：2026-03-23 19:33
 * @Description：
 */
public interface AdminBlogSettingsService {
    /**
     * 更新博客设置信息
     * @param updateBlogSettingsReqVO
     * @return
     */
    Response updateBlogSettings(UpdateBlogSettingsReqVO updateBlogSettingsReqVO);

    Response findDetail();

}
