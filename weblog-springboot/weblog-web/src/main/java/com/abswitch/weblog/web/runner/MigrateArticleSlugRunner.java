package com.abswitch.weblog.web.runner;

import com.abswitch.weblog.common.config.BaiduTranslateProperties;
import com.abswitch.weblog.common.domain.dos.ArticleDO;
import com.abswitch.weblog.common.domain.mapper.ArticleMapper;
import com.abswitch.weblog.common.utils.SlugUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@Order(Integer.MAX_VALUE)
public class MigrateArticleSlugRunner implements CommandLineRunner {

    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private BaiduTranslateProperties baiduTranslateProperties;

    @Override
    public void run(String... args) throws Exception {
        log.info("==> 开始迁移文章 slug...");

        List<ArticleDO> articles = articleMapper.selectList(
                Wrappers.<ArticleDO>lambdaQuery()
                        .eq(ArticleDO::getSlug, "")
                        .or()
                        .isNull(ArticleDO::getSlug)
        );

        if (articles.isEmpty()) {
            log.info("==> 所有文章已有 slug，无需迁移");
            return;
        }

        for (ArticleDO article : articles) {
            String baseSlug = SlugUtil.generateSlug(article.getTitle(),
                    baiduTranslateProperties.getAppId(), baiduTranslateProperties.getSecretKey());
            String slug = SlugUtil.ensureUnique(baseSlug, articleMapper::existsBySlug);

            articleMapper.updateById(ArticleDO.builder()
                    .id(article.getId())
                    .slug(slug)
                    .build());

            log.info("==> 文章 ID: {}，标题: {}，生成 slug: {}", article.getId(), article.getTitle(), slug);

            // 百度翻译 API 有 QPS 限制（标准版 1 QPS），加延迟避免被限流
            Thread.sleep(1100);
        }

        log.info("==> 文章 slug 迁移完成，共处理 {} 篇文章", articles.size());
    }
}
