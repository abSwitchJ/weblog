package com.abswitch.weblog.admin.service.impl;

import com.abswitch.weblog.admin.service.AdminStatisticsService;
import com.abswitch.weblog.common.domain.dos.ArticleTagRelDO;
import com.abswitch.weblog.common.domain.dos.TagDO;
import com.abswitch.weblog.common.domain.mapper.ArticleTagRelMapper;
import com.abswitch.weblog.common.domain.mapper.TagMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Maps;
import com.abswitch.weblog.common.domain.dos.ArticleCategoryRelDO;
import com.abswitch.weblog.common.domain.dos.CategoryDO;
import com.abswitch.weblog.common.domain.mapper.ArticleCategoryRelMapper;
import com.abswitch.weblog.common.domain.mapper.CategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author：abSwitch
 * @url：
 * @date：2026-03-31 19:51
 * @Description：
 */

@Service
@Slf4j
public class AdminStatisticsImpl implements AdminStatisticsService {

    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private ArticleCategoryRelMapper articleCategoryRelMapper;

    @Autowired
    private TagMapper tagMapper;
    @Autowired
    private ArticleTagRelMapper articleTagRelMapper;

    @Override
    public void statisticsCategoryArticleTotal() {
        // 查询所有分类
        List<CategoryDO> categoryDOS = categoryMapper.selectList(Wrappers.emptyWrapper());

        // 查询所有文章-分类映射记录
        List<ArticleCategoryRelDO> articleCategoryRelDOS = articleCategoryRelMapper.selectList(Wrappers.emptyWrapper());

        // 按所属分类 ID 进行分组
        Map<Long, List<ArticleCategoryRelDO>> categoryIdAndArticleCategoryRelDOMap = Maps.newHashMap();
        // 如果不为空
        if (!CollectionUtils.isEmpty(articleCategoryRelDOS)) {
            categoryIdAndArticleCategoryRelDOMap = articleCategoryRelDOS.stream()
                    .collect(Collectors.groupingBy(ArticleCategoryRelDO::getCategoryId));
        }

        if (!CollectionUtils.isEmpty(categoryDOS)) {
            // 循环统计各分类下的文章总数
            for (CategoryDO categoryDO : categoryDOS) {
                Long categoryId = categoryDO.getId();
                // 获取此分类下所有映射记录
                List<ArticleCategoryRelDO> articleCategoryRelDOList = categoryIdAndArticleCategoryRelDOMap.get(categoryId);

                // 获取文章总数
                int articlesTotal = CollectionUtils.isEmpty(articleCategoryRelDOList) ? 0 : articleCategoryRelDOList.size();

                // 更新该分类的文章总数
                CategoryDO updateCategoryDO = CategoryDO.builder()
                        .id(categoryId)
                        .articlesTotal(articlesTotal)
                        .build();
                categoryMapper.updateById(updateCategoryDO);
            }
        }
    }

    @Override
    public void statisticsTagArticleTotal() {

        List<TagDO> tagDOS = tagMapper.selectList(Wrappers.emptyWrapper());

        List<ArticleTagRelDO> articleTagRelDOS = articleTagRelMapper.selectList(Wrappers.emptyWrapper());

        Map<Long, List<ArticleTagRelDO>> tagIdAndArticleTagRelDOMap = Maps.newHashMap();
        if (!CollectionUtils.isEmpty(articleTagRelDOS)) {
            tagIdAndArticleTagRelDOMap = articleTagRelDOS.stream()
                    .collect(Collectors.groupingBy(ArticleTagRelDO::getTagId));
        }

        if (!CollectionUtils.isEmpty(tagDOS)) {

            for (TagDO tagDO : tagDOS) {

                List<ArticleTagRelDO> articleTagRelDOList = tagIdAndArticleTagRelDOMap.get(tagDO.getId());

                int articlesTotal = CollectionUtils.isEmpty(articleTagRelDOList) ? 0 : articleTagRelDOList.size();

                TagDO updateTagDO = TagDO.builder().id(tagDO.getId()).articlesTotal(articlesTotal).build();

                tagMapper.updateById(updateTagDO);
            }
        }
    }
}


