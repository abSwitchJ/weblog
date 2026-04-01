package com.abswitch.weblog.admin.service;

/**
 * @Author：abSwitch
 * @url：
 * @date：2026-03-31 19:49
 * @Description：
 */
public interface AdminStatisticsService {
    /**
     * 统计各分类下文章总数
     */
    void statisticsCategoryArticleTotal();

    /**
     * 统计各标签下文章总数
     */
    void statisticsTagArticleTotal();
}
