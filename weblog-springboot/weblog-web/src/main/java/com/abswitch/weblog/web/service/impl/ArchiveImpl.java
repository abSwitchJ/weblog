package com.abswitch.weblog.web.service.impl;

import com.abswitch.weblog.common.domain.dos.ArticleDO;
import com.abswitch.weblog.common.domain.mapper.ArticleMapper;
import com.abswitch.weblog.common.service.translation.TranslationService;
import com.abswitch.weblog.common.utils.Response;
import com.abswitch.weblog.web.convert.ArticleConvert;
import com.abswitch.weblog.web.model.vo.archive.FindArchiveArticlePageListRspVO;
import com.abswitch.weblog.web.model.vo.archive.FindArchiveArticleRspVO;
import com.abswitch.weblog.web.model.vo.archive.FindArchiveListReqVO;
import com.abswitch.weblog.web.service.ArchiveService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ArchiveImpl implements ArchiveService {

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private TranslationService translationService;

    @Override
    public Response findArchiveList(FindArchiveListReqVO reqVO) {

        List<ArticleDO> articleDOS = articleMapper.selectAllForArchive();

        if (articleDOS.isEmpty()) {
            return Response.ok(Collections.emptyList());
        }

        List<FindArchiveArticleRspVO> vos = articleDOS.stream()
                .map(ArticleConvert.INSTANCE::convertDO2ArchiveVO).toList();

        // 翻译标题
        if ("en".equalsIgnoreCase(reqVO.getLang())) {
            List<String> titles = vos.stream().map(FindArchiveArticleRspVO::getTitle).toList();
            Map<String, String> map = translationService.getTranslations(titles, "zh", "en");
            vos.forEach(v -> {
                String t = map.get(v.getTitle());
                if (t != null) v.setTitle(t);
            });
        }

        Map<Integer, List<FindArchiveArticleRspVO>> yearListMap = vos.stream()
                .collect(Collectors.groupingBy(vo -> vo.getCreateDate().getYear()));

        TreeMap<Integer, List<FindArchiveArticleRspVO>> sortedMap = new TreeMap<>(Collections.reverseOrder());
        sortedMap.putAll(yearListMap);

        List<FindArchiveArticlePageListRspVO> result = Lists.newArrayList();

        sortedMap.forEach((year, articles) ->
                result.add(FindArchiveArticlePageListRspVO.builder().year(year).articles(articles).build()));

        return Response.ok(result);
    }
}
