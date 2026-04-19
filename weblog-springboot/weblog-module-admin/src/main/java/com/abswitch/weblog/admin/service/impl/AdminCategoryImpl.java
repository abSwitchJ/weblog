package com.abswitch.weblog.admin.service.impl;

import com.abswitch.weblog.admin.model.vo.SelectRspVO;
import com.abswitch.weblog.admin.model.vo.category.*;
import com.abswitch.weblog.admin.service.AdminCategoryService;
import com.abswitch.weblog.common.domain.dos.ArticleCategoryRelDO;
import com.abswitch.weblog.common.domain.dos.CategoryDO;
import com.abswitch.weblog.common.domain.mapper.ArticleCategoryRelMapper;
import com.abswitch.weblog.common.domain.mapper.CategoryMapper;
import com.abswitch.weblog.common.emuns.ResponseCodeEnum;
import com.abswitch.weblog.common.exception.BizException;
import com.abswitch.weblog.common.service.translation.PreTranslateAsyncService;
import com.abswitch.weblog.common.utils.PageResponse;
import com.abswitch.weblog.common.utils.Response;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Author：abSwitch
 * @url：
 * @date：2026-03-21 17:45
 * @Description：
 */
@Service
@Slf4j
public class AdminCategoryImpl implements AdminCategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private ArticleCategoryRelMapper articleCategoryRelMapper;

    @Autowired
    private PreTranslateAsyncService preTranslateAsyncService;

    @Override
    public Response addCategory(AddCategoryReqVO addCategoryReqVO) {

        String categoryName = addCategoryReqVO.getName().trim();

        CategoryDO categoryDO = categoryMapper.selectByName(categoryName);

        if (Objects.nonNull(categoryDO)) {
            log.warn("分类名称： {}, 此分类已存在", categoryName);
            throw new BizException(ResponseCodeEnum.CATEGORY_NAME_IS_EXISTED);
        }

        CategoryDO insterCategoryDO = CategoryDO.builder().name(categoryName).updateTime(LocalDateTime.now()).build();

        categoryMapper.insert(insterCategoryDO);

        // 异步预翻译分类名
        preTranslateAsyncService.preTranslateText(categoryName);
        return Response.ok();
    }

    @Override
    public PageResponse findCategoryList(FindCategoryPageListReqVO findCategoryPageListReqVO) {

        // 获取当前页、以及每页需要展示的数据数量
        Long current = findCategoryPageListReqVO.getCurrent();
        Long size = findCategoryPageListReqVO.getSize();

        // 分页对象(查询第几页、每页多少数据)
        Page<CategoryDO> page = new Page<>(current, size);

        // 构建查询条件
        LambdaQueryWrapper<CategoryDO> wrapper = new LambdaQueryWrapper<>();

        String name = findCategoryPageListReqVO.getName();
        LocalDate startDate = findCategoryPageListReqVO.getStartDate();
        LocalDate endDate = findCategoryPageListReqVO.getEndDate();

        wrapper
                .like(StringUtils.isNotBlank(name), CategoryDO::getName, name.trim()) // like 模块查询
                .ge(Objects.nonNull(startDate), CategoryDO::getCreateTime, startDate) // 大于等于 startDate
                .le(Objects.nonNull(endDate), CategoryDO::getCreateTime, endDate)  // 小于等于 endDate
                .orderByDesc(CategoryDO::getCreateTime); // 按创建时间倒叙

        // 执行分页查询 查询当前页
        Page<CategoryDO> categoryDOPage = categoryMapper.selectPage(page, wrapper);

        List<CategoryDO> categoryDOS = categoryDOPage.getRecords();

        // DO 转 VO
        List<FindCategoryPageListRspVO> vos = null;
        if (!CollectionUtils.isEmpty(categoryDOS)) {
            vos = categoryDOS.stream()
                    .map(categoryDO -> FindCategoryPageListRspVO.builder()
                            .id(categoryDO.getId())
                            .name(categoryDO.getName())
                            .createTime(categoryDO.getCreateTime())
                            .articlesTotal(categoryDO.getArticlesTotal())
                            .build())
                    .collect(Collectors.toList());
        }

        return PageResponse.ok(categoryDOPage, vos);
    }

    @Override
    public Response updateCategory(UpdateCategoryReqVO updateCategoryReqVO) {
        Long id = updateCategoryReqVO.getId();
        String newName = updateCategoryReqVO.getName().trim();

        // 校验分类是否存在
        CategoryDO existCategoryDO = categoryMapper.selectById(id);
        if (Objects.isNull(existCategoryDO)) {
            log.warn("分类不存在，id: {}", id);
            throw new BizException(ResponseCodeEnum.CATEGORY_NOT_EXISTED);
        }

        // 名称未变化，直接返回
        if (Objects.equals(existCategoryDO.getName(), newName)) {
            return Response.ok();
        }

        // 校验新名称是否已被其他分类占用
        CategoryDO sameNameCategoryDO = categoryMapper.selectByName(newName);
        if (Objects.nonNull(sameNameCategoryDO) && !Objects.equals(sameNameCategoryDO.getId(), id)) {
            log.warn("分类名称： {}, 此分类已存在", newName);
            throw new BizException(ResponseCodeEnum.CATEGORY_NAME_IS_EXISTED);
        }

        CategoryDO updateCategoryDO = CategoryDO.builder()
                .id(id)
                .name(newName)
                .updateTime(LocalDateTime.now())
                .build();
        categoryMapper.updateById(updateCategoryDO);

        // 异步预翻译新分类名
        preTranslateAsyncService.preTranslateText(newName);
        return Response.ok();
    }

    @Override
    public Response deleteCategory(DeleteCategoryReqVO deleteCategoryReqVO) {
        // 分类 ID
        Long categoryId = deleteCategoryReqVO.getId();

        // 校验该分类下是否已经有文章，若有，则提示需要先删除分类下所有文章，才能删除
        ArticleCategoryRelDO articleCategoryRelDO = articleCategoryRelMapper.selectOneByCategoryId(categoryId);

        if (Objects.nonNull(articleCategoryRelDO)) {
            log.warn("==> 此分类下包含文章，无法删除，categoryId: {}", categoryId);
            throw new BizException(ResponseCodeEnum.CATEGORY_CAN_NOT_DELETE);
        }

        // 删除分类
        categoryMapper.deleteById(categoryId);

        return Response.ok();
    }

    @Override
    public Response findCategorySelectList() {
        // 查询所有分类
        List<CategoryDO> categoryDOS = categoryMapper.selectList(null);

        // DO 转 VO
        List<SelectRspVO> selectRspVOS = null;
        // 如果分类数据不为空
        if (!CollectionUtils.isEmpty(categoryDOS)) {
            // 将分类 ID 作为 Value 值，将分类名称作为 label 展示
            selectRspVOS = categoryDOS.stream()
                    .map(categoryDO -> SelectRspVO.builder()
                            .label(categoryDO.getName())
                            .value(categoryDO.getId())
                            .build())
                    .collect(Collectors.toList());
        }

        return Response.ok(selectRspVOS);
    }

}
