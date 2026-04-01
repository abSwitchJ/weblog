package com.abswitch.weblog.web.service.impl;

import com.abswitch.weblog.common.domain.dos.*;
import com.abswitch.weblog.common.domain.mapper.ArticleMapper;
import com.abswitch.weblog.common.domain.mapper.ArticleTagRelMapper;
import com.abswitch.weblog.common.domain.mapper.TagMapper;
import com.abswitch.weblog.common.emuns.ResponseCodeEnum;
import com.abswitch.weblog.common.exception.BizException;
import com.abswitch.weblog.common.utils.PageResponse;
import com.abswitch.weblog.common.utils.Response;
import com.abswitch.weblog.web.convert.ArticleConvert;
import com.abswitch.weblog.web.convert.TagConvert;
import com.abswitch.weblog.web.model.vo.category.FindCategoryArticlePageListRspVO;
import com.abswitch.weblog.web.model.vo.category.FindCategoryListReqVO;
import com.abswitch.weblog.web.model.vo.tag.FindTagArticlePageListReqVO;
import com.abswitch.weblog.web.model.vo.tag.FindTagArticlePageListRspVO;
import com.abswitch.weblog.web.model.vo.tag.FindTagListReqVO;
import com.abswitch.weblog.web.model.vo.tag.FindTagListRspVO;
import com.abswitch.weblog.web.service.TagService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Author：abSwitch
 * @url：
 * @date：2026-03-26 11:00
 * @Description：
 */
@Service
@Slf4j
public class TagImpl implements TagService {

    @Autowired
    private TagMapper tagMapper;

    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private ArticleTagRelMapper articleTagRelMapper;

    @Override
    public Response findTagList(FindTagListReqVO findTagListReqVO) {
        Long size = findTagListReqVO.getSize();

        List<TagDO> tagDOS = null;
        // 如果接口入参中未指定 size
        if (Objects.isNull(size) || size == 0) {
            // 查询所有分类
            tagDOS = tagMapper.selectList(Wrappers.emptyWrapper());
        } else {
            // 否则查询指定的数量
            tagDOS = tagMapper.selectByLimit(size);
        }
        List<FindTagListRspVO> findTagListRspVOS = tagDOS.stream().map(TagConvert.INSTANCE::convertDO2VO).collect(Collectors.toList());

        return Response.ok(findTagListRspVOS);
    }

    @Override
    public Response findTagArticlePageList(FindTagArticlePageListReqVO findTagArticlePageListReqVO) {


        Long tagId = findTagArticlePageListReqVO.getId();
        Long current = findTagArticlePageListReqVO.getCurrent();
        Long size = findTagArticlePageListReqVO.getSize();

        TagDO tagDO = tagMapper.selectById(tagId);

        // 判断该标签是否存在
        if (Objects.isNull(tagDO)) {
            log.warn("==> 该标签不存在, tagId: {}", tagId);
            throw new BizException(ResponseCodeEnum.TAG_NOT_EXISTED);
        }

        List<ArticleTagRelDO> tagRelDOS = articleTagRelMapper.selectListByTagId(tagId);

        if (tagRelDOS.isEmpty()) {
            log.info("==> 该标签下还未发布任何文章, tagId: {}", tagId);
            return PageResponse.ok(null, null);
        }

        List<Long> articleIds = tagRelDOS.stream().map(ArticleTagRelDO::getArticleId).toList();

        Page<ArticleDO> articleDOPage = articleMapper.selectPageListByArticleIds(current, size, articleIds);

        List<ArticleDO> articleDOS = articleDOPage.getRecords();

        if (articleDOS.isEmpty()) {
            log.info("==> 该分类下还未发布任何文章, tagId: {}", tagId);
            return PageResponse.ok(null, null);
        }

        List<FindTagArticlePageListRspVO> findTagArticlePageListRspVOS = articleDOS.stream()
                .map(ArticleConvert.INSTANCE::convertDO2TagVO).toList();

        return PageResponse.ok(articleDOPage, findTagArticlePageListRspVOS);
    }
}
