package com.abswitch.weblog.search.index;

/**
 * @Author：abSwitch
 * @url：
 * @date：2026-03-30 15:34
 * @Description：
 */
public interface ArticleIndex {
    /**
     * 索引名称
     */
    String NAME = "article";

    // --------------------- 文档字段 ---------------------
    String COLUMN_ID = "id";

    String COLUMN_TITLE = "title";

    String COLUMN_COVER = "cover";

    String COLUMN_SUMMARY = "summary";

    String COLUMN_CONTENT = "content";

    String COLUMN_CREATE_TIME = "createTime";

    String COLUMN_SLUG = "slug";
}
