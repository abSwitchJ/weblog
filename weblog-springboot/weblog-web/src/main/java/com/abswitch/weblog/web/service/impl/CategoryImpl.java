package com.abswitch.weblog.web.service.impl;

import com.abswitch.weblog.common.emuns.ResponseCodeEnum;
import com.abswitch.weblog.common.exception.BizException;
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

import java.util.List;
import java.util.Objects;

/**
 * @Author：abSwitch
 * @url：
 * @date：2026-03-26 10:25
 * @Description：
 */
@Service
@Slf4j
public class CategoryImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private ArticleCategoryRelMapper articleCategoryRelMapper;

    @Autowired
    private ArticleMapper articleMapper;

    @Override
    public Response findCategoryList(FindCategoryOrTagListReqVO findCategoryListReqVO) {

        Long size = findCategoryListReqVO.getSize();

        List<CategoryDO> categoryDOS = null;
        // 如果接口入参中未指定 size
        if (Objects.isNull(size) || size == 0) {
            // 查询所有分类
            categoryDOS = categoryMapper.selectList(Wrappers.emptyWrapper());
        } else {
            // 否则查询指定的数量
            categoryDOS = categoryMapper.selectByLimit(size);
        }
        List<FindCategoryOrTagListRspVO> findCategoryOrTagListRspVOS = categoryDOS.stream()
                .map(CategoryConvert.INSTANCE::convertDO2VO).toList();



        return Response.ok(findCategoryOrTagListRspVOS);
    }

    @Override
    public Response findCategoryArticlePageList(FindCategoryOrTagOrArticlePageListReqVO findCategoryArticlePageListReqVO) {

        Long categoryId = findCategoryArticlePageListReqVO.getId();
        Long current = findCategoryArticlePageListReqVO.getCurrent();
        Long size = findCategoryArticlePageListReqVO.getSize();

        CategoryDO categoryDO = categoryMapper.selectById(categoryId);

        // 判断该分类是否存在
        if (Objects.isNull(categoryDO)) {
            log.warn("==> 该分类不存在, categoryId: {}", categoryId);
            throw new BizException(ResponseCodeEnum.CATEGORY_NOT_EXISTED);
        }

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

        List<FindCategoryOrTagArticlePageListRspVO> findCategoryOrTagArticlePageListRspVOS = articleDOS.stream()
                .map(ArticleConvert.INSTANCE::convertDO2CategoryVO).toList();

        return PageResponse.ok(articleDOPage, findCategoryOrTagArticlePageListRspVOS);
    }
}
