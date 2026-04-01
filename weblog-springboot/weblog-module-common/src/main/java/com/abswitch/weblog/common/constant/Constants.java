package com.abswitch.weblog.common.constant;

import java.time.format.DateTimeFormatter;

/**
 * @Author：abSwitch
 * @url：
 * @date：2026-03-26 22:27
 * @Description：
 */

public interface Constants {
    /**
     * 月-日 格式
     */
    DateTimeFormatter MONTH_DAY_FORMATTER = DateTimeFormatter.ofPattern("MM-dd");
    /**
     * 年-月-日 小时-分钟-秒
     */
    DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
}
