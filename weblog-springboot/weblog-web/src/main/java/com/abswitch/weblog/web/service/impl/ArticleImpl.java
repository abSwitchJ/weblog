package com.abswitch.weblog.web.service.impl;

import com.abswitch.weblog.admin.event.ReadArticleEvent;
import com.abswitch.weblog.common.domain.dos.*;
import com.abswitch.weblog.common.domain.mapper.*;
import com.abswitch.weblog.common.emuns.ResponseCodeEnum;
import com.abswitch.weblog.common.exception.BizException;
import com.abswitch.weblog.common.service.translation.TranslationService;
import com.abswitch.weblog.common.utils.PageResponse;
import com.abswitch.weblog.common.utils.Response;
import com.abswitch.weblog.web.convert.ArticleConvert;
import com.abswitch.weblog.web.markdown.MarkdownHelper;
import com.abswitch.weblog.web.model.vo.FindIndexArticleOrArchivePageListReqVO;
import com.abswitch.weblog.web.model.vo.FindCategoryOrTagListRspVO;
import com.abswitch.weblog.web.model.vo.article.ArticleRspVO;
import com.abswitch.weblog.web.model.vo.article.FindArticleDetailBySlugReqVO;
import com.abswitch.weblog.web.model.vo.article.FindArticleDetailRspVO;
import com.abswitch.weblog.web.model.vo.article.FindIndexArticlePageListRspVO;
import com.abswitch.weblog.web.model.vo.article.TocItemVO;
import com.abswitch.weblog.web.service.ArticleService;
import com.abswitch.weblog.web.utils.MarkdownStatsUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ArticleImpl implements ArticleService {

    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private TagMapper tagMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private ArticleCategoryRelMapper articleCategoryRelMapper;
    @Autowired
    private ArticleTagRelMapper articleTagRelMapper;
    @Autowired
    private ArticleContentMapper articleContentMapper;
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    @Autowired
    private TranslationService translationService;

    @Override
    public Response findArticlePageList(FindIndexArticleOrArchivePageListReqVO findIndexArticlePageListReqVO) {

        Long current = findIndexArticlePageListReqVO.getCurrent();
        Long size = findIndexArticlePageListReqVO.getSize();

        Page<ArticleDO> page = articleMapper
                .selectPageList(new Page<>(current, size), null, null, null);

        List<ArticleDO> articleDOS = page.getRecords();

        if (articleDOS.isEmpty()) {
            return PageResponse.ok(page, Collections.emptyList());
        }

        List<FindIndexArticlePageListRspVO> vos = articleDOS.stream()
                .map(ArticleConvert.INSTANCE::convertDO2VO)
                .collect(Collectors.toList());

        // 中英文切换：翻译标题 + 摘要
        if ("en".equalsIgnoreCase(findIndexArticlePageListReqVO.getLang())) {
            List<String> sources = new ArrayList<>();
            for (FindIndexArticlePageListRspVO v : vos) {
                if (v.getTitle() != null) sources.add(v.getTitle());
                if (v.getSummary() != null) sources.add(v.getSummary());
            }
            Map<String, String> map = translationService.getTranslations(sources, "zh", "en");
            vos.forEach(v -> {
                String t = map.get(v.getTitle());
                if (t != null) v.setTitle(t);
                String s = map.get(v.getSummary());
                if (s != null) v.setSummary(s);
            });
        }

        return PageResponse.ok(page, vos);
    }

    @Override
    public Response findArticleDetailBySlug(FindArticleDetailBySlugReqVO findArticleDetailBySlugReqVO) {

        String slug = findArticleDetailBySlugReqVO.getSlug();
        boolean isEn = "en".equalsIgnoreCase(findArticleDetailBySlugReqVO.getLang());

        ArticleDO articleDO = articleMapper.selectBySlug(slug);
        Long articleId = articleDO != null ? articleDO.getId() : null;

        if (Objects.isNull(articleDO)) {
            log.warn("==> 该文章不存在, slug: {}", slug);
            throw new BizException(ResponseCodeEnum.ARTICLE_NOT_FOUND);
        }

        String content = articleContentMapper.selectByArticleId(articleId).getContent();
        String originalContent = content;
        String title = articleDO.getTitle();

        if (isEn) {
            String translatedContent = translationService.getCachedOrNull(content, "zh", "en");
            if (translatedContent != null) content = MarkdownHelper.normalizeHeadings(translatedContent);
            String translatedTitle = translationService.getCachedOrNull(title, "zh", "en");
            if (translatedTitle != null) title = translatedTitle;
        }

        int totalWords = MarkdownStatsUtil.calculateWordCount(content);
        String contentHtml = MarkdownHelper.convertMarkdown2Html(content);
        List<TocItemVO> toc = MarkdownHelper.extractToc(contentHtml);

        if (isEn) {
            if (toc.isEmpty() && !content.equals(originalContent)) {
                String originalHtml = MarkdownHelper.convertMarkdown2Html(originalContent);
                toc = MarkdownHelper.extractToc(originalHtml);
            }
            if (!toc.isEmpty()) {
                List<String> texts = toc.stream().map(TocItemVO::getText).collect(Collectors.toList());
                Map<String, String> translationMap = translationService.getTranslations(texts, "zh", "en");
                toc.forEach(item -> {
                    String cached = translationMap.get(item.getText());
                    if (cached != null) {
                        item.setText(cached);
                    } else {
                        // 缓存未命中时实时翻译并写入缓存，避免目录保持中文
                        String tr = translationService.translateAndCache(item.getText(), "zh", "en");
                        if (tr != null) item.setText(tr);
                    }
                });
            }
        }

        String readTime = MarkdownStatsUtil.calculateReadingTime(totalWords, isEn ? "en" : "zh");

        Long categoryId = articleCategoryRelMapper.selectByArticleId(articleId).getCategoryId();
        String categoryName = categoryMapper.selectById(categoryId).getName();
        if (isEn) {
            String t = translationService.getCachedOrNull(categoryName, "zh", "en");
            if (t != null) categoryName = t;
        }

        List<Long> tagIds = articleTagRelMapper
                .selectByArticleId(articleId)
                .stream()
                .map(ArticleTagRelDO::getTagId)
                .toList();

        List<TagDO> tagDOS = tagMapper.selectByIds(tagIds);
        List<FindCategoryOrTagListRspVO> tagListRspVOS = tagDOS.stream().map(tagDO ->
                        FindCategoryOrTagListRspVO.builder()
                                .id(tagDO.getId())
                                .name(tagDO.getName())
                                .build()).toList();
        if (isEn && !tagListRspVOS.isEmpty()) {
            List<String> names = tagListRspVOS.stream().map(FindCategoryOrTagListRspVO::getName).toList();
            Map<String, String> map = translationService.getTranslations(names, "zh", "en");
            tagListRspVOS.forEach(t -> {
                String tr = map.get(t.getName());
                if (tr != null) t.setName(tr);
            });
        }

        FindArticleDetailRspVO findArticleDetailRspVO = FindArticleDetailRspVO.builder()
                .title(title)
                .createDate(LocalDate.from(articleDO.getCreateTime()))
                .readNum(articleDO.getReadNum())
                .content(contentHtml)
                .categoryId(categoryId)
                .categoryName(categoryName)
                .tags(tagListRspVOS)
                .totalWords(totalWords)
                .readTime(readTime)
                .toc(toc)
                .build();

        ArticleDO preArticleDO = articleMapper.selectPreArticle(articleDO.getCreateTime());
        if (Objects.nonNull(preArticleDO)) {
            ArticleRspVO preArticle = ArticleConvert.INSTANCE.convertDO2ArticleVO(preArticleDO);
            if (isEn && preArticle.getArticleTitle() != null) {
                String t = translationService.getCachedOrNull(preArticle.getArticleTitle(), "zh", "en");
                if (t != null) preArticle.setArticleTitle(t);
            }
            findArticleDetailRspVO.setPreArticle(preArticle);
        }

        ArticleDO nextArticleDO = articleMapper.selectNextArticle(articleDO.getCreateTime());
        if (Objects.nonNull(nextArticleDO)) {
            ArticleRspVO nextArticle = ArticleConvert.INSTANCE.convertDO2ArticleVO(nextArticleDO);
            if (isEn && nextArticle.getArticleTitle() != null) {
                String t = translationService.getCachedOrNull(nextArticle.getArticleTitle(), "zh", "en");
                if (t != null) nextArticle.setArticleTitle(t);
            }
            findArticleDetailRspVO.setNextArticle(nextArticle);
        }

        // 发布文章阅读事件
        eventPublisher.publishEvent(new ReadArticleEvent(this, articleId));

        return Response.ok(findArticleDetailRspVO);
    }
}
