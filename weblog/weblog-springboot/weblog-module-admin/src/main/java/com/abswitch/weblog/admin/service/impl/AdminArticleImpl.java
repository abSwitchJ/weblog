package com.abswitch.weblog.admin.service.impl;

import com.abswitch.weblog.admin.convert.ArticleConvert;
import com.abswitch.weblog.admin.event.DeleteArticleEvent;
import com.abswitch.weblog.admin.event.PublishArticleEvent;
import com.abswitch.weblog.admin.event.UpdateArticleEvent;
import com.abswitch.weblog.admin.model.vo.article.*;
import com.abswitch.weblog.admin.service.AdminArticleService;
import com.abswitch.weblog.common.domain.dos.*;
import com.abswitch.weblog.common.domain.mapper.*;
import com.abswitch.weblog.common.emuns.ResponseCodeEnum;
import com.abswitch.weblog.common.exception.BizException;
import com.abswitch.weblog.common.utils.PageResponse;
import com.abswitch.weblog.common.utils.Response;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author：abSwitch
 * @url：
 * @date：2026-03-23 21:10
 * @Description：
 */
@Service
@Slf4j
public class AdminArticleImpl implements AdminArticleService {

    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private ArticleContentMapper articleContentMapper;
    @Autowired
    private ArticleCategoryRelMapper articleCategoryRelMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private TagMapper tagMapper;
    @Autowired
    private ArticleTagRelMapper articleTagRelMapper;
    @Autowired
    private ArticleConvert articleConvert;
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    /**
     * 发布文章
     *
     * @param publishArticleReqVO
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response publishArticle(PublishArticleReqVO publishArticleReqVO) {
        // 1. VO 转 ArticleDO, 并保存
        ArticleDO articleDO = articleConvert.convertVO2DO(publishArticleReqVO);
        articleMapper.insert(articleDO);

        // 拿到插入记录的主键 ID
        Long articleId = articleDO.getId();

        // 2. VO 转 ArticleContentDO，并保存
        ArticleContentDO articleContentDO = articleConvert.convertVO2ContentDO(publishArticleReqVO);
        articleContentDO.setArticleId(articleId);
        articleContentMapper.insert(articleContentDO);

        // 3. 处理文章关联的分类
        Long categoryId = publishArticleReqVO.getCategoryId();

        // 3.1 校验提交的分类是否真实存在
        CategoryDO categoryDO = categoryMapper.selectById(categoryId);
        if (Objects.isNull(categoryDO)) {
            log.warn("==> 分类不存在, categoryId: {}", categoryId);
            throw new BizException(ResponseCodeEnum.CATEGORY_NOT_EXISTED);
        }

        ArticleCategoryRelDO articleCategoryRelDO = ArticleCategoryRelDO.builder()
                .articleId(articleId)
                .categoryId(categoryId)
                .build();
        articleCategoryRelMapper.insert(articleCategoryRelDO);

        // 4. 保存文章关联的标签集合
        List<String> publishTags = publishArticleReqVO.getTags();
        insertTags(articleId, publishTags);
        // 发送文章发布事件
        eventPublisher.publishEvent(new PublishArticleEvent(this, articleId));
        return Response.ok();
    }

    /**
     * 删除文章
     * @param deleteArticleReqVO
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response deleteArticle(DeleteArticleReqVO deleteArticleReqVO) {

        Long articleId = deleteArticleReqVO.getId();
        articleMapper.deleteById(articleId);
        articleContentMapper.deleteByArticleId(articleId);
        articleCategoryRelMapper.deleteByArticleId(articleId);
        articleTagRelMapper.deleteByArticleId(articleId);

        // 发布文章删除事件
        eventPublisher.publishEvent(new DeleteArticleEvent(this, articleId));
        return Response.ok();
    }

    /**
     * 查询文章分页数据
     * @param findArticlePageListReqVO
     * @return
     */
    @Override
    public Response findArticlePageList(FindArticlePageListReqVO findArticlePageListReqVO) {
        // 获取当前页、以及每页需要展示的数据数量
        Long current = findArticlePageListReqVO.getCurrent();
        Long size = findArticlePageListReqVO.getSize();
        Page<ArticleDO> page = new Page<>(current, size);

        String title = findArticlePageListReqVO.getTitle();
        LocalDate startDate = findArticlePageListReqVO.getStartDate();
        LocalDate endDate = findArticlePageListReqVO.getEndDate();

        Page<ArticleDO> articleDOPage = articleMapper.selectPageList(page, title, startDate, endDate);
        List<ArticleDO> articleDOS = articleDOPage.getRecords();

        // DO 转 VO
        List<FindArticlePageListRspVO> vos = articleDOS.stream().map(
                articleDO -> articleConvert.convertArticleDO2VO(articleDO))
                .collect(Collectors.toList());

        return PageResponse.ok(articleDOPage, vos);
    }

    @Override
    public Response findArticleDetail(FindArticleDetailReqVO findArticleDetailReqVO) {

        Long articleId = findArticleDetailReqVO.getId();

        ArticleDO articleDO = articleMapper.selectById(articleId);

        if (Objects.isNull(articleDO)) {
            log.warn("==> 查询的文章不存在，articleId: {}", articleId);
            throw new BizException(ResponseCodeEnum.ARTICLE_NOT_FOUND);
        }

        ArticleContentDO articleContentDO = articleContentMapper.selectByArticleId(articleId);

        // 所属分类
        ArticleCategoryRelDO articleCategoryRelDO = articleCategoryRelMapper.selectByArticleId(articleId);

        // 对应标签
        List<ArticleTagRelDO> articleTagRelDOS = articleTagRelMapper.selectByArticleId(articleId);
        // 获取对应标签 ID 集合
        List<Long> tagIds = articleTagRelDOS.stream().map(ArticleTagRelDO::getTagId).collect(Collectors.toList());

        // DO 转 VO
        FindArticleDetailRspVO vo = articleConvert.convertDO2VO(articleDO);
        vo.setContent(articleContentDO.getContent());
        vo.setCategoryId(articleCategoryRelDO.getCategoryId());
        vo.setTagIds(tagIds);

        return Response.ok(vo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response updateArticle(UpdateArticleReqVO updateArticleReqVO) {
        Long articleId = updateArticleReqVO.getId();

        ArticleDO articleDO = articleConvert.convertUpdateVO2DO(updateArticleReqVO);
        articleDO.setUpdateTime(LocalDateTime.now());

        int count = articleMapper.updateById(articleDO);

        // 根据更新是否成功，来判断该文章是否存在
        if (count == 0) {
            log.warn("==> 该文章不存在, articleId: {}", articleId);
            throw new BizException(ResponseCodeEnum.ARTICLE_NOT_FOUND);
        }


        // 2. VO 转 ArticleContentDO，并更新
        ArticleContentDO articleContentDO = articleConvert.convertUpdateVO2ContentDO(updateArticleReqVO);
        articleContentDO.setArticleId(articleId);
        articleContentMapper.updateByArticleId(articleContentDO);

        // 3. 更新文章分类
        Long categoryId = updateArticleReqVO.getCategoryId();

        // 3.1 校验提交的分类是否真实存在
        CategoryDO categoryDO = categoryMapper.selectById(categoryId);
        if (Objects.isNull(categoryDO)) {
            log.warn("==> 分类不存在, categoryId: {}", categoryId);
            throw new BizException(ResponseCodeEnum.CATEGORY_NOT_EXISTED);
        }

        // 先删除该文章关联的分类记录，再插入新的关联关系
        articleCategoryRelMapper.deleteByArticleId(articleId);
        ArticleCategoryRelDO articleCategoryRelDO = ArticleCategoryRelDO.builder()
                .articleId(articleId)
                .categoryId(categoryId)
                .build();
        articleCategoryRelMapper.insert(articleCategoryRelDO);

        // 4. 保存文章关联的标签集合
        // 先删除该文章对应的标签
        articleTagRelMapper.deleteByArticleId(articleId);
        List<String> publishTags = updateArticleReqVO.getTags();
        insertTags(articleId, publishTags);
        // 发布文章修改事件
        eventPublisher.publishEvent(new UpdateArticleEvent(this, articleId));
        return Response.ok();
    }

    @Override
    public Response isTopUpdateArticle(IsTopUpdateArticleReqVO isTopUpdateArticleReqVO) {

        Long articleId = isTopUpdateArticleReqVO.getId();
        Boolean isTop = isTopUpdateArticleReqVO.getIsTop();
        // 默认权重为 0
        Integer weight = 0;
        // 若设置为置顶
        if (isTop) {
            // 查询出表中最大的权重值
            ArticleDO articleDO = articleMapper.selectMaxWeight();
            Integer maxWeight = articleDO.getWeight();
            // 最大权重值加一
            weight = maxWeight + 1;
        }

        // 更新该篇文章的权重值
        articleMapper.updateById(ArticleDO.builder()
                .id(articleId)
                .weight(weight)
                .build());

        return Response.ok();
    }

    /**
     * 保存标签
     *
     * @param articleId
     * @param publishTags
     */
    private void insertTags(Long articleId, List<String> publishTags) {
        // 筛选提交的标签（表中不存在的标签）
        Set<String> notExistTags = new HashSet<>();  // 存储已存在标签 ID
        // 筛选提交的标签（表中已存在的标签）
        Set<String> existedTags = new HashSet<>();  // 存储待插入的新标签名称（小写，自动去重）

        // 查询出所有标签
        List<TagDO> tagDOS = tagMapper.selectList(null);
        // 按字符串名称提交上来的标签，也有可能是表中已存在的，比如表中已经有了 Java 标签，用户提交了个 java 小写的标签，需要内部装换为 Java 标签
        Map<String, Long> tagNameIdMap = tagDOS.stream()
                .collect(Collectors.toMap(
                        tagDO -> tagDO.getName().toLowerCase(),
                        TagDO::getId,
                        (v1, v2) -> v1   // 如果重复，保留第一个
                ));

            for (String tagDO : publishTags) {
                if (StringUtils.isBlank(tagDO)) {
                    continue;
                }
                String tagName = tagDO.toLowerCase();
                Long id = tagNameIdMap.get(tagName);
                if (id != null) {
                    existedTags.add(String.valueOf(id));   // 记录 ID
                } else {
                    notExistTags.add(tagName);   // 统一以小写存储，避免重复
                }
            }

        // 将提交的上来的，不存在于表中的标签，入库保存
        if (!CollectionUtils.isEmpty(notExistTags)) {
            List<TagDO> newTags = notExistTags.stream()
                .map(tagName -> TagDO.builder()
                        .name(tagName)
                        .createTime(LocalDateTime.now())
                        .updateTime(LocalDateTime.now())
                        .build())
                .collect(Collectors.toList());

            tagMapper.insertBatch(newTags);   // 批量插入，主键自动回填
            // 将新标签 ID 加入集合
            newTags.forEach(tag -> existedTags.add(String.valueOf(tag.getId())));
        }

        // 将提交的上来的，已存在于表中的标签，文章-标签关联关系入库
        if (!CollectionUtils.isEmpty(existedTags)) {
            List<ArticleTagRelDO> articleTagRelDOS = existedTags.stream()
                    .map(tagId -> ArticleTagRelDO.builder()
                            .articleId(articleId)
                            .tagId(Long.valueOf(tagId))
                            .build())
                    .collect(Collectors.toList());

            // 批量插入
            articleTagRelMapper.insertBatchSomeColumn(articleTagRelDOS);
        }
    }
}