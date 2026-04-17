package com.abswitch.weblog.admin.service.impl;

import com.abswitch.weblog.admin.model.vo.SelectRspVO;
import com.abswitch.weblog.admin.model.vo.tag.AddTagsReqVO;
import com.abswitch.weblog.admin.model.vo.tag.DeleteTagReqVO;
import com.abswitch.weblog.admin.model.vo.tag.FindTagPageListReqVO;
import com.abswitch.weblog.admin.model.vo.tag.FindTagPageListRspVO;
import com.abswitch.weblog.admin.service.AdminTagService;
import com.abswitch.weblog.common.domain.dos.ArticleTagRelDO;
import com.abswitch.weblog.common.domain.dos.TagDO;
import com.abswitch.weblog.common.domain.mapper.ArticleTagRelMapper;
import com.abswitch.weblog.common.domain.mapper.TagMapper;
import com.abswitch.weblog.common.emuns.ResponseCodeEnum;
import com.abswitch.weblog.common.exception.BizException;
import com.abswitch.weblog.common.service.translation.PreTranslateAsyncService;
import com.abswitch.weblog.common.utils.PageResponse;
import com.abswitch.weblog.common.utils.Response;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.baomidou.mybatisplus.extension.toolkit.Db.saveBatch;

/**
 * @Author：abSwitch
 * @url：
 * @date：2026-03-22 14:24
 * @Description：
 */
@Service
@Slf4j
public class AdminTagImpl implements AdminTagService {

    @Autowired
    private TagMapper tagMapper;

    @Autowired
    private ArticleTagRelMapper articleTagRelMapper;

    @Autowired
    private PreTranslateAsyncService preTranslateAsyncService;
    /**
     * 添加标签集合
     *
     * @param addTagsReqVO
     * @return
     */
    @Override
    public Response addTags(AddTagsReqVO addTagsReqVO) {

        // vo 转 do
        List<TagDO> tagDOS = addTagsReqVO.getTags().stream()
                .map(tagName -> TagDO.builder()
                        .name(tagName.trim()) // 去掉前后空格
                        .createTime(LocalDateTime.now())
                        .updateTime(LocalDateTime.now())
                        .build())
                .collect(Collectors.toList());

        // 批量插入
        try {
            tagMapper.insertBatch(tagDOS);

        } catch (Exception e) {
            log.warn("该标签已存在", e);
        }

        // 异步预翻译标签名
        preTranslateAsyncService.preTranslateTexts(
                tagDOS.stream().map(TagDO::getName).collect(Collectors.toList()));
        return Response.ok();
    }

    @Override
    public PageResponse findTagList(FindTagPageListReqVO findTagPageListReqVO) {

        Long current = findTagPageListReqVO.getCurrent();
        Long size = findTagPageListReqVO.getSize();

        Page<TagDO> page = new Page<>(current, size);

        LambdaQueryWrapper<TagDO> wrapper = new LambdaQueryWrapper<>();

        String name = findTagPageListReqVO.getName().trim();
        LocalDate startTime = findTagPageListReqVO.getStartTime();
        LocalDate endTime = findTagPageListReqVO.getEndTime();

        wrapper.like(Strings.isNotBlank(name), TagDO::getName, name)
                .ge(Objects.nonNull(startTime), TagDO::getCreateTime, startTime)
                .le(Objects.nonNull(endTime), TagDO::getUpdateTime, endTime);

        Page<TagDO> tagDOPage = tagMapper.selectPage(page, wrapper);

        List<TagDO> tagDOS = tagDOPage.getRecords();

        List<FindTagPageListRspVO> vos = null;
        if (!CollectionUtils.isEmpty(tagDOS)) {
            vos = tagDOS.stream()
                    .map(tagDO -> FindTagPageListRspVO.builder()
                            .id(tagDO.getId())
                            .createTime(tagDO.getCreateTime())
                            .name(tagDO.getName())
                            .articlesTotal(tagDO.getArticlesTotal())
                            .build())
                    .collect(Collectors.toList());
        }

        return PageResponse.ok(tagDOPage, vos);
    }

    /**
     * 删除标签
     *
     * @param deleteTagReqVO
     * @return
     */
    @Override
    public Response deleteTag(DeleteTagReqVO deleteTagReqVO) {
        // 标签 ID
        Long tagId = deleteTagReqVO.getId();

        // 校验该标签下是否有关联的文章，若有，则不允许删除，提示用户需要先删除标签下的文章
        ArticleTagRelDO articleTagRelDO = articleTagRelMapper.selectOneByTagId(tagId);

        if (Objects.nonNull(articleTagRelDO)) {
            log.warn("==> 此标签下包含文章，无法删除，tagId: {}", tagId);
            throw new BizException(ResponseCodeEnum.TAG_CAN_NOT_DELETE);
        }

        // 根据标签 ID 删除
        int count = tagMapper.deleteById(tagId);

        return count == 1 ? Response.ok() : Response.fail(ResponseCodeEnum.TAG_NOT_EXISTED);
    }

    @Override
    public Response findTagSelectList() {

        List<TagDO> tagDOS = tagMapper.selectList(null);

        // DO 转 VO
        List<SelectRspVO> selectRspVOS = null;

        if (!CollectionUtils.isEmpty(tagDOS)) {
            selectRspVOS = tagDOS.stream()
                    .map(tagDO -> SelectRspVO.builder()
                            .label(tagDO.getName())
                            .value(tagDO.getId())
                            .build())
                    .collect(Collectors.toList());
        }

        return Response.ok(selectRspVOS);
    }
}
