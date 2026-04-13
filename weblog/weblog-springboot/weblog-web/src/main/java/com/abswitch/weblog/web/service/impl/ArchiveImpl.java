package com.abswitch.weblog.web.service.impl;

import com.abswitch.weblog.common.domain.dos.ArticleDO;
import com.abswitch.weblog.common.domain.mapper.ArticleMapper;
import com.abswitch.weblog.common.utils.PageResponse;
import com.abswitch.weblog.common.utils.Response;
import com.abswitch.weblog.web.convert.ArticleConvert;
import com.abswitch.weblog.web.model.vo.FindIndexArticleOrArchivePageListReqVO;
import com.abswitch.weblog.web.model.vo.archive.FindArchiveArticlePageListRspVO;
import com.abswitch.weblog.web.model.vo.archive.FindArchiveArticleRspVO;
import com.abswitch.weblog.web.service.ArchiveService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author：abSwitch
 * @url：
 * @date：2026-03-26 13:56
 * @Description：
 */
@Service
@Slf4j
public class ArchiveImpl implements ArchiveService {

    @Autowired
    private ArticleMapper articleMapper;

    @Override
    public Response findArchivePageList(FindIndexArticleOrArchivePageListReqVO findArchiveArticlePageListReqVO) {

        Long current = findArchiveArticlePageListReqVO.getCurrent();
        Long size = findArchiveArticlePageListReqVO.getSize();

        Page<ArticleDO> page = articleMapper.selectPageList(new Page<>(current, size), null, null, null);

        List<ArticleDO> articleDOS = page.getRecords();

        if (articleDOS.isEmpty()) {
            return PageResponse.ok(page, Collections.emptyList());
        }

        List<FindArchiveArticleRspVO> findArchiveArticleRspVOS = articleDOS.stream()
                .map(ArticleConvert.INSTANCE::convertDO2ArchiveVO).toList();

        Map<YearMonth, List<FindArchiveArticleRspVO>> monthListMap = findArchiveArticleRspVOS.stream()
                .collect(Collectors.groupingBy(FindArchiveArticleRspVO::getCreateMonth));

        TreeMap<YearMonth, List<FindArchiveArticleRspVO>> sortedMap = new TreeMap<>(Collections.reverseOrder());
        sortedMap.putAll(monthListMap);

        List<FindArchiveArticlePageListRspVO> findArchiveArticlePageListRspVOS = Lists.newArrayList();

        sortedMap
                .forEach((k, v) ->
                        findArchiveArticlePageListRspVOS
                                .add(FindArchiveArticlePageListRspVO.builder().month(k).articles(v).build()));

        return PageResponse.ok(page,findArchiveArticlePageListRspVOS);
    }
}
