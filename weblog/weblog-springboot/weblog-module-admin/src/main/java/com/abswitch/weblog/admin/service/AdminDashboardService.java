package com.abswitch.weblog.admin.service;

import com.abswitch.weblog.common.utils.Response;

/**
 * @Author：abSwitch
 * @url：
 * @date：2026-03-26 20:54
 * @Description：
 */
public interface AdminDashboardService {
    Response findDashboardStatistics();

    Response findPublishArticleStatistics();

    Response findDashboardPVStatistics();
}
