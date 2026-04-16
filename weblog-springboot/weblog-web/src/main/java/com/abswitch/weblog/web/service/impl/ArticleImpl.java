package com.abswitch.weblog.web.service.impl;

import com.abswitch.weblog.admin.event.ReadArticleEvent;
import com.abswitch.weblog.common.domain.dos.*;
import com.abswitch.weblog.common.domain.mapper.*;
import com.abswitch.weblog.common.emuns.ResponseCodeEnum;
import com.abswitch.weblog.common.exception.BizException;
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
import com.abswitch.weblog.web.service.ArticleService;
import com.abswitch.weblog.web.utils.MarkdownStatsUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Author：abSwitch
 * @url：
 * @date：2026-03-25 14:02
 * @Description：
 */
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

    @Override
    public Response findArticlePageList(FindIndexArticleOrArchivePageListReqVO findIndexArticlePageListReqVO) {

        Long current = findIndexArticlePageListReqVO.getCurrent();
        Long size = findIndexArticlePageListReqVO.getSize();

        // 第一步：分页查询文章主体记录
        Page<ArticleDO> page = articleMapper
                .selectPageList(new Page<>(current, size), null, null, null);

        List<ArticleDO> articleDOS = page.getRecords();

        if (articleDOS.isEmpty()) {
            return PageResponse.ok(page, Collections.emptyList());
        }

        // 文章 DO 转 VO（仅 id, title, createDate, summary）
        List<FindIndexArticlePageListRspVO> vos = articleDOS.stream()
                .map(ArticleConvert.INSTANCE::convertDO2VO)
                .collect(Collectors.toList());

        return PageResponse.ok(page, vos);
    }

    @Override
    public Response findArticleDetailBySlug(FindArticleDetailBySlugReqVO findArticleDetailBySlugReqVO) {

        String slug = findArticleDetailBySlugReqVO.getSlug();

        ArticleDO articleDO = articleMapper.selectBySlug(slug);
        Long articleId = articleDO != null ? articleDO.getId() : null;

        // 判断文章是否存在
        if (Objects.isNull(articleDO)) {
            log.warn("==> 该文章不存在, slug: {}", slug);
            throw new BizException(ResponseCodeEnum.ARTICLE_NOT_FOUND);
        }

        String content = articleContentMapper.selectByArticleId(articleId).getContent();
        log.info(content);
        int totalWords = MarkdownStatsUtil.calculateWordCount(content);
        String contentHtml = MarkdownHelper.convertMarkdown2Html(content);


        String readTime = MarkdownStatsUtil.calculateReadingTime(totalWords);

        Long categoryId = articleCategoryRelMapper.selectByArticleId(articleId).getCategoryId();
        String categoryName = categoryMapper.selectById(categoryId).getName();

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

        FindArticleDetailRspVO findArticleDetailRspVO = FindArticleDetailRspVO.builder()
                .title(articleDO.getTitle())
                .createDate(LocalDate.from(articleDO.getCreateTime()))
                .readNum(articleDO.getReadNum())
                .content(contentHtml)
                .categoryId(categoryId)
                .categoryName(categoryName)
                .tags(tagListRspVOS)
                .totalWords(totalWords)
                .readTime(readTime)
                .build();

        ArticleDO preArticleDO = articleMapper.selectPreArticle(articleDO.getCreateTime());
        if (Objects.nonNull(preArticleDO)) {

            ArticleRspVO preArticle = ArticleConvert.INSTANCE.convertDO2ArticleVO(preArticleDO);
            findArticleDetailRspVO.setPreArticle(preArticle);
        }

        ArticleDO nextArticleDO = articleMapper.selectNextArticle(articleDO.getCreateTime());
        if (Objects.nonNull(nextArticleDO)) {

            ArticleRspVO nextArticle = ArticleConvert.INSTANCE.convertDO2ArticleVO(nextArticleDO);
            findArticleDetailRspVO.setNextArticle(nextArticle);

        }


        // 发布文章阅读事件
        eventPublisher.publishEvent(new ReadArticleEvent(this, articleId));

        return Response.ok(findArticleDetailRspVO);
    }
}
