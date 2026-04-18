package com.abswitch.weblog.web.service.impl;

import com.abswitch.weblog.common.domain.dos.*;
import com.abswitch.weblog.common.domain.mapper.ArticleMapper;
import com.abswitch.weblog.common.domain.mapper.ArticleTagRelMapper;
import com.abswitch.weblog.common.domain.mapper.TagMapper;
import com.abswitch.weblog.common.emuns.ResponseCodeEnum;
import com.abswitch.weblog.common.exception.BizException;
import com.abswitch.weblog.common.service.translation.TranslationService;
import com.abswitch.weblog.common.utils.PageResponse;
import com.abswitch.weblog.common.utils.Response;
import com.abswitch.weblog.web.convert.ArticleConvert;
import com.abswitch.weblog.web.convert.TagConvert;
import com.abswitch.weblog.web.model.vo.FindCategoryOrTagOrArticlePageListReqVO;
import com.abswitch.weblog.web.model.vo.FindCategoryOrTagArticlePageListRspVO;
import com.abswitch.weblog.web.model.vo.FindCategoryOrTagListReqVO;
import com.abswitch.weblog.web.model.vo.FindCategoryOrTagListRspVO;
import com.abswitch.weblog.web.model.vo.FindTagArticleByNameReqVO;
import com.abswitch.weblog.web.model.vo.FindTagNameReqVO;
import com.abswitch.weblog.web.service.TagService;
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
import java.util.stream.Collectors;

@Service
@Slf4j
public class TagImpl implements TagService {

    @Autowired
    private TagMapper tagMapper;

    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private ArticleTagRelMapper articleTagRelMapper;

    @Autowired
    private TranslationService translationService;

    @Override
    public Response findTagList(FindCategoryOrTagListReqVO findCategoryOrTagListReqVO) {
        Long size = findCategoryOrTagListReqVO.getSize();

        List<TagDO> tagDOS;
        if (Objects.isNull(size) || size == 0) {
            tagDOS = tagMapper.selectList(Wrappers.emptyWrapper());
        } else {
            tagDOS = tagMapper.selectByLimit(size);
        }
        List<FindCategoryOrTagListRspVO> rspVOS = tagDOS.stream().map(TagConvert.INSTANCE::convertDO2VO).collect(Collectors.toList());

        translateNames(rspVOS, findCategoryOrTagListReqVO.getLang());

        return Response.ok(rspVOS);
    }

    @Override
    public Response findTagArticlePageList(FindCategoryOrTagOrArticlePageListReqVO findTagArticlePageListReqVO) {

        Long tagId = findTagArticlePageListReqVO.getId();
        Long current = findTagArticlePageListReqVO.getCurrent();
        Long size = findTagArticlePageListReqVO.getSize();

        TagDO tagDO = tagMapper.selectById(tagId);

        if (Objects.isNull(tagDO)) {
            log.warn("==> 该标签不存在, tagId: {}", tagId);
            throw new BizException(ResponseCodeEnum.TAG_NOT_EXISTED);
        }

        return doFindArticlePageByTagId(tagId, current, size, findTagArticlePageListReqVO.getLang());
    }

    @Override
    public Response findTagArticlePageListByName(FindTagArticleByNameReqVO reqVO) {
        TagDO tagDO = resolveTagDO(reqVO.getName());
        return doFindArticlePageByTagId(tagDO.getId(), reqVO.getCurrent(), reqVO.getSize(), reqVO.getLang());
    }

    @Override
    public Response resolveTagName(FindTagNameReqVO reqVO) {
        TagDO tagDO = resolveTagDO(reqVO.getName());
        String zh = tagDO.getName();
        String en = translationService.translateAndCache(zh, "zh", "en");
        Map<String, String> result = new HashMap<>();
        result.put("zhName", zh);
        result.put("enName", en != null ? en : zh);
        return Response.ok(result);
    }

    private TagDO resolveTagDO(String name) {
        TagDO tagDO = tagMapper.selectByName(name);
        if (Objects.isNull(tagDO)) {
            String zhName = translationService.getSourceByTranslated(name, "zh", "en");
            if (zhName != null) {
                tagDO = tagMapper.selectByName(zhName);
            }
        }
        if (Objects.isNull(tagDO)) {
            log.warn("==> 该标签不存在, tagName: {}", name);
            throw new BizException(ResponseCodeEnum.TAG_NOT_EXISTED);
        }
        return tagDO;
    }

    private Response doFindArticlePageByTagId(Long tagId, Long current, Long size, String lang) {
        List<ArticleTagRelDO> tagRelDOS = articleTagRelMapper.selectListByTagId(tagId);

        if (tagRelDOS.isEmpty()) {
            log.info("==> 该标签下还未发布任何文章, tagId: {}", tagId);
            return PageResponse.ok(null, null);
        }

        List<Long> articleIds = tagRelDOS.stream().map(ArticleTagRelDO::getArticleId).toList();

        Page<ArticleDO> articleDOPage = articleMapper.selectPageListByArticleIds(current, size, articleIds);

        List<ArticleDO> articleDOS = articleDOPage.getRecords();

        if (articleDOS.isEmpty()) {
            log.info("==> 该标签下还未发布任何文章, tagId: {}", tagId);
            return PageResponse.ok(null, null);
        }

        List<FindCategoryOrTagArticlePageListRspVO> vos = articleDOS.stream()
                .map(ArticleConvert.INSTANCE::convertDO2TagVO).collect(Collectors.toList());

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
