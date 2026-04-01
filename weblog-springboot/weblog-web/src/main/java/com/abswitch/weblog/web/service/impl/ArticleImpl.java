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
import com.abswitch.weblog.web.model.vo.article.*;
import com.abswitch.weblog.web.model.vo.category.FindCategoryListRspVO;
import com.abswitch.weblog.web.model.vo.tag.FindTagListRspVO;
import com.abswitch.weblog.web.service.ArticleService;
import com.abswitch.weblog.web.utils.MarkdownStatsUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

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
    public Response findArticlePageList(FindIndexArticlePageListReqVO findIndexArticlePageListReqVO) {

        Long current = findIndexArticlePageListReqVO.getCurrent();
        Long size = findIndexArticlePageListReqVO.getSize();

        // 第一步：分页查询文章主体记录
        Page<ArticleDO> page = articleMapper
                .selectPageList(new Page<>(current, size), null, null, null);

        List<ArticleDO> articleDOS = page.getRecords();

        if (articleDOS.isEmpty()) {
            return PageResponse.ok(page, Collections.emptyList());
        }

        List<FindIndexArticlePageListRspVO> vos = null;

        // 文章 DO 转 VO
        vos = articleDOS.stream()
                .map(ArticleConvert.INSTANCE::convertDO2VO)
                .collect(Collectors.toList());

        List<Long> articleIds = articleDOS.stream().map(ArticleDO::getId).collect(Collectors.toList());

        // 第二步：设置文章所属分类
        // 查询所有分类
        List<CategoryDO> categoryDOS = categoryMapper.selectList(Wrappers.emptyWrapper());
        // 转 Map, 方便后续根据分类 ID 拿到对应的分类名称
        Map<Long, String> categoryIdNameMap = categoryDOS.stream().collect(Collectors.toMap(CategoryDO::getId, CategoryDO::getName));

        // 第三步：设置文章标签
        // 查询所有标签
        List<TagDO> tagDOS = tagMapper.selectList(Wrappers.emptyWrapper());
        // 转 Map, 方便后续根据标签 ID 拿到对应的标签名称
        Map<Long, String> tagIdNameMap = tagDOS.stream().collect(Collectors.toMap(TagDO::getId, TagDO::getName));

        // 文章-分类关联关系按 articleId 分组
        List<ArticleCategoryRelDO> articleCategoryRelDOS = articleCategoryRelMapper.selectByArticleIds(articleIds);
        Map<Long, List<ArticleCategoryRelDO>> categoryRelMap = articleCategoryRelDOS.stream()
                .collect(Collectors.groupingBy(ArticleCategoryRelDO::getArticleId));

        // 文章-标签关联关系按 articleId 分组
        List<ArticleTagRelDO> articleTagRelDOS = articleTagRelMapper.selectByArticleIds(articleIds);
        Map<Long, List<ArticleTagRelDO>> tagRelMap = articleTagRelDOS.stream()
                .collect(Collectors.groupingBy(ArticleTagRelDO::getArticleId));

        vos.forEach(vo -> {
            Long articleId = vo.getId();

            List<ArticleCategoryRelDO> categoryRelDOS = categoryRelMap.get(articleId);

            List<ArticleTagRelDO> tagRelDOS = tagRelMap.get(articleId);

            if (categoryRelDOS != null && !categoryRelDOS.isEmpty()) {
                // 一篇文章最多对应一个分类（假设业务如此）
                ArticleCategoryRelDO categoryRelDO = categoryRelDOS.getFirst();
                Long categoryId = categoryRelDO.getCategoryId();
                String categoryName = categoryIdNameMap.get(categoryId);
                if (categoryName != null) {
                    FindCategoryListRspVO categoryVO = FindCategoryListRspVO.builder()
                            .id(categoryId)
                            .name(categoryName)
                            .build();
                    vo.setCategory(categoryVO);
                }
            }
            if (tagRelDOS != null && !tagRelDOS.isEmpty()) {
                List<FindTagListRspVO> tagVOs = tagRelDOS.stream()
                        .map(tagRelDO -> {
                            Long tagId = tagRelDO.getTagId();
                            String tagName = tagIdNameMap.get(tagId);
                            return FindTagListRspVO.builder()
                                    .id(tagId)
                                    .name(tagName)
                                    .build();
                        })
                        .collect(Collectors.toList());
                vo.setTags(tagVOs);
            } else {
                // 没有标签时设置空列表（避免前端 NPE）
                vo.setTags(Collections.emptyList());
            }
        });
        return PageResponse.ok(page, vos);
    }

    @Override
    public Response findArticleDetail(FindArticleDetailReqVO findArticleDetailReqVO) {

        Long articleId = findArticleDetailReqVO.getId();

        ArticleDO articleDO = articleMapper.selectById(articleId);

        // 判断文章是否存在
        if (Objects.isNull(articleDO)) {
            log.warn("==> 该文章不存在, articleId: {}", articleId);
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
        List<FindTagListRspVO> tagListRspVOS = tagDOS.stream().map(tagDO ->
                        FindTagListRspVO.builder()
                                .id(tagDO.getId())
                                .name(tagDO.getName())
                                .build()).toList();

        FindArticleDetailRspVO findArticleDetailRspVO = FindArticleDetailRspVO.builder()
                .title(articleDO.getTitle())
                .createTime(articleDO.getCreateTime())
                .readNum(articleDO.getReadNum())
                .content(contentHtml)
                .categoryId(categoryId)
                .categoryName(categoryName)
                .tags(tagListRspVOS)
                .totalWords(totalWords)
                .readTime(readTime)
                .build();

        ArticleDO preArticleDO = articleMapper.selectPreArticle(articleId);
        if (Objects.nonNull(preArticleDO)) {

            ArticleRspVO preArticle = ArticleConvert.INSTANCE.convertDO2ArticleVO(preArticleDO);
            findArticleDetailRspVO.setPreArticle(preArticle);
        }

        ArticleDO nextArticleDO = articleMapper.selectNextArticle(articleId);
        if (Objects.nonNull(nextArticleDO)) {

            ArticleRspVO nextArticle = ArticleConvert.INSTANCE.convertDO2ArticleVO(nextArticleDO);
            findArticleDetailRspVO.setNextArticle(nextArticle);

        }


        // 发布文章阅读事件
        eventPublisher.publishEvent(new ReadArticleEvent(this, articleId));

        return Response.ok(findArticleDetailRspVO);
    }
}
