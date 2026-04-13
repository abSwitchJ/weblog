package com.abswitch.weblog.web.service.impl;

import com.abswitch.weblog.common.domain.mapper.ArticleMapper;
import com.abswitch.weblog.common.domain.mapper.CategoryMapper;
import com.abswitch.weblog.common.domain.mapper.TagMapper;
import com.abswitch.weblog.common.utils.Response;
import com.abswitch.weblog.web.model.vo.statistics.FindStatisticsInfoRspVO;
import com.abswitch.weblog.web.service.StatisticsService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @Author：abSwitch
 * @url：
 * @date：2026-03-30 14:14
 * @Description：
 */
@Service
@Slf4j
public class StatisticsImpl implements StatisticsService {

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private TagMapper tagMapper;

    @Autowired
    private CategoryMapper categoryMapper;



    @Override
    public Response findArticlePageList() {

        Long articleTotalCount = articleMapper.selectCount(Wrappers.emptyWrapper());

        Long tagTotalCount = tagMapper.selectCount(Wrappers.emptyWrapper());

        Long categoryTotalCount = categoryMapper.selectCount(Wrappers.emptyWrapper());

        List<Long> allReadNum = articleMapper.selectAllReadNum();
        Long pvTotalCount = allReadNum.stream()
                .filter(Objects::nonNull)      // 过滤掉 null 值，避免 NPE
                .mapToLong(Long::longValue)    // 转换为 long 基本类型流
                .sum();                        // 求和

        FindStatisticsInfoRspVO findStatisticsInfoRspVO = FindStatisticsInfoRspVO.builder().articleTotalCount(articleTotalCount).categoryTotalCount(categoryTotalCount).tagTotalCount(tagTotalCount).pvTotalCount(pvTotalCount).build();

        return Response.ok(findStatisticsInfoRspVO);
    }
}
