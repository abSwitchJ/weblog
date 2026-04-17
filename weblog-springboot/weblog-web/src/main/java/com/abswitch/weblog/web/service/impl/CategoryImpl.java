package com.abswitch.weblog.web.service.impl;

import com.abswitch.weblog.common.emuns.ResponseCodeEnum;
import com.abswitch.weblog.common.exception.BizException;
import com.abswitch.weblog.common.service.translation.TranslationService;
import com.abswitch.weblog.common.utils.PageResponse;
import com.abswitch.weblog.web.convert.ArticleConvert;
import com.abswitch.weblog.common.domain.dos.ArticleCategoryRelDO;
import com.abswitch.weblog.common.domain.dos.ArticleDO;
import com.abswitch.weblog.common.domain.dos.CategoryDO;
import com.abswitch.weblog.common.domain.mapper.ArticleCategoryRelMapper;
import com.abswitch.weblog.common.domain.mapper.ArticleMapper;
import com.abswitch.weblog.common.domain.mapper.CategoryMapper;
import com.abswitch.weblog.common.utils.Response;
import com.abswitch.weblog.web.convert.CategoryConvert;
import com.abswitch.weblog.web.model.vo.FindCategoryArticleByNameReqVO;
import com.abswitch.weblog.web.model.vo.FindCategoryNameReqVO;
import com.abswitch.weblog.web.model.vo.FindCategoryOrTagOrArticlePageListReqVO;
import com.abswitch.weblog.web.model.vo.FindCategoryOrTagArticlePageListRspVO;
import com.abswitch.weblog.web.model.vo.FindCategoryOrTagListReqVO;
import com.abswitch.weblog.web.model.vo.FindCategoryOrTagListRspVO;
import com.abswitch.weblog.web.service.CategoryService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
public class CategoryImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private ArticleCategoryRelMapper articleCategoryRelMapper;

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private TranslationService translationService;

    @Override
    public Response findCategoryList(FindCategoryOrTagListReqVO findCategoryListReqVO) {

        Long size = findCategoryListReqVO.getSize();

        List<CategoryDO> categoryDOS;
        if (Objects.isNull(size) || size == 0) {
            categoryDOS = categoryMapper.selectList(Wrappers.emptyWrapper());
        } else {
            categoryDOS = categoryMapper.selectByLimit(size);
        }
        List<FindCategoryOrTagListRspVO> rspVOS = categoryDOS.stream()
                .map(CategoryConvert.INSTANCE::convertDO2VO).collect(java.util.stream.Collectors.toList());

        translateNames(rspVOS, findCategoryListReqVO.getLang());

        return Response.ok(rspVOS);
    }

    @Override
    public Response findCategoryArticlePageList(FindCategoryOrTagOrArticlePageListReqVO findCategoryArticlePageListReqVO) {

        Long categoryId = findCategoryArticlePageListReqVO.getId();
        Long current = findCategoryArticlePageListReqVO.getCurrent();
        Long size = findCategoryArticlePageListReqVO.getSize();

        CategoryDO categoryDO = categoryMapper.selectById(categoryId);

        if (Objects.isNull(categoryDO)) {
            log.warn("==> 该分类不存在, categoryId: {}", categoryId);
            throw new BizException(ResponseCodeEnum.CATEGORY_NOT_EXISTED);
        }

        return doFindArticlePageByCategoryId(categoryId, current, size, findCategoryArticlePageListReqVO.getLang());
    }

    @Override
    public Response findCategoryArticlePageListByName(FindCategoryArticleByNameReqVO reqVO) {
        CategoryDO categoryDO = resolveCategoryDO(reqVO.getName());
        return doFindArticlePageByCategoryId(categoryDO.getId(), reqVO.getCurrent(), reqVO.getSize(), reqVO.getLang());
    }

    @Override
    public Response resolveCategoryName(FindCategoryNameReqVO reqVO) {
        CategoryDO categoryDO = resolveCategoryDO(reqVO.getName());
        String zh = categoryDO.getName();
        String en = translationService.translateAndCache(zh, "zh", "en");
        Map<String, String> result = new HashMap<>();
        result.put("zhName", zh);
        result.put("enName", en != null ? en : zh);
        return Response.ok(result);
    }

    private CategoryDO resolveCategoryDO(String name) {
        CategoryDO categoryDO = categoryMapper.selectByName(name);
        if (Objects.isNull(categoryDO)) {
            String zhName = translationService.getSourceByTranslated(name, "zh", "en");
            if (zhName != null) {
                categoryDO = categoryMapper.selectByName(zhName);
            }
        }
        if (Objects.isNull(categoryDO)) {
            log.warn("==> 该分类不存在, categoryName: {}", name);
            throw new BizException(ResponseCodeEnum.CATEGORY_NOT_EXISTED);
        }
        return categoryDO;
    }

    private Response doFindArticlePageByCategoryId(Long categoryId, Long current, Long size, String lang) {
        List<ArticleCategoryRelDO> categoryRelDOS = articleCategoryRelMapper.selectListByCategoryId(categoryId);

        if (categoryRelDOS.isEmpty()) {
            log.info("==> 该分类下还未发布任何文章, categoryId: {}", categoryId);
            return PageResponse.ok(null, null);
        }

        List<Long> articleIds = categoryRelDOS.stream().map(ArticleCategoryRelDO::getArticleId).toList();

        Page<ArticleDO> articleDOPage = articleMapper.selectPageListByArticleIds(current, size, articleIds);

        List<ArticleDO> articleDOS = articleDOPage.getRecords();

        if (articleDOS.isEmpty()) {
            log.info("==> 该分类下还未发布任何文章, categoryId: {}", categoryId);
            return PageResponse.ok(null, null);
        }

        List<FindCategoryOrTagArticlePageListRspVO> vos = articleDOS.stream()
                .map(ArticleConvert.INSTANCE::convertDO2CategoryVO).collect(java.util.stream.Collectors.toList());

        translateArticleListVOs(vos, lang);

        return PageResponse.ok(articleDOPage, vos);
    }

    private void translateNames(List<FindCategoryOrTagListRspVO> vos, String lang) {
        if (!"en".equalsIgnoreCase(lang) || vos.isEmpty()) return;
        List<String> names = vos.stream().map(FindCategoryOrTagListRspVO::getName).toList();
        Map<String, String> map = translationService.getTranslations(names, "zh", "en");
        vos.forEach(v -> {
            String t = map.get(v.getName());
            if (t != null) v.setName(t);
        });
    }

    private void translateArticleListVOs(List<FindCategoryOrTagArticlePageListRspVO> vos, String lang) {
        if (!"en".equalsIgnoreCase(lang) || vos.isEmpty()) return;
        List<String> sources = new ArrayList<>();
        for (FindCategoryOrTagArticlePageListRspVO v : vos) {
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
}
