package com.abswitch.weblog.web.runner;

import com.abswitch.weblog.common.domain.dos.ArticleContentDO;
import com.abswitch.weblog.common.domain.dos.ArticleDO;
import com.abswitch.weblog.common.domain.dos.CategoryDO;
import com.abswitch.weblog.common.domain.dos.TagDO;
import com.abswitch.weblog.common.domain.mapper.ArticleContentMapper;
import com.abswitch.weblog.common.domain.mapper.ArticleMapper;
import com.abswitch.weblog.common.domain.mapper.CategoryMapper;
import com.abswitch.weblog.common.domain.mapper.TagMapper;
import com.abswitch.weblog.common.service.translation.TranslationService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 启动时对所有现有文章/分类/标签做预翻译。
 * 已缓存的会跳过；未缓存的逐条翻译，每次百度 API 调用之间由 TranslationService 内部限速。
 * 该 Runner 顺序在 MigrateArticleSlugRunner 之后。
 */
@Component
@Slf4j
@Order(Integer.MAX_VALUE)
public class PreTranslateRunner implements CommandLineRunner {

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private ArticleContentMapper articleContentMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private TagMapper tagMapper;

    @Autowired
    private TranslationService translationService;

    @Override
    public void run(String... args) {
        // 后台异步执行，避免阻塞 Spring 启动（首次运行可能耗时较长）
        Thread thread = new Thread(this::doPreTranslate, "PreTranslateRunner");
        thread.setDaemon(true);
        thread.start();
    }

    private void doPreTranslate() {
        log.info("==> [PreTranslate] 启动预翻译...");
        int total = 0;

        // 1. 文章标题/摘要/正文
        List<ArticleDO> articles = articleMapper.selectList(Wrappers.emptyWrapper());
        for (ArticleDO article : articles) {
            total += translateField(article.getTitle());
            total += translateField(article.getSummary());
            ArticleContentDO contentDO = articleContentMapper.selectByArticleId(article.getId());
            if (contentDO != null) {
                total += translateField(contentDO.getContent());
            }
        }

        // 2. 分类名
        List<CategoryDO> categories = categoryMapper.selectList(Wrappers.emptyWrapper());
        for (CategoryDO c : categories) {
            total += translateField(c.getName());
        }

        // 3. 标签名
        List<TagDO> tags = tagMapper.selectList(Wrappers.emptyWrapper());
        for (TagDO t : tags) {
            total += translateField(t.getName());
        }

        log.info("==> [PreTranslate] 完成。处理调用次数: {}（含缓存命中）", total);
    }

    /**
     * 触发一次 translateAndCache。命中缓存即返回，无 API 调用与延迟。
     * 未命中则真正调用百度 API（含 1.1s 限速）。
     */
    private int translateField(String text) {
        if (text == null || text.isBlank()) {
            return 0;
        }
        try {
            translationService.translateAndCache(text, "zh", "en");
            return 1;
        } catch (Exception e) {
            log.warn("[PreTranslate] 翻译失败: {}", e.getMessage());
            return 0;
        }
    }
}
