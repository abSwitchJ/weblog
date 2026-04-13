package com.abswitch.weblog.web.convert;

import com.abswitch.weblog.common.domain.dos.ArticleDO;
import com.abswitch.weblog.web.model.vo.archive.FindArchiveArticleRspVO;
import com.abswitch.weblog.web.model.vo.article.ArticleRspVO;
import com.abswitch.weblog.web.model.vo.article.FindIndexArticlePageListRspVO;
import com.abswitch.weblog.web.model.vo.FindCategoryOrTagArticlePageListRspVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * @Author：abSwitch
 * @url：
 * @date：2026-03-25 14:46
 * @Description：
 */
@Mapper
public interface ArticleConvert {

    /**
     * 初始化 convert 实例
     */
    ArticleConvert INSTANCE = Mappers.getMapper(ArticleConvert.class);
    //DO转VO
    @Mapping(target = "isTop", expression = "java(articleDO.getWeight()>0)")
    @Mapping(target = "createDate", expression = "java(java.time.LocalDate.from(articleDO.getCreateTime()))")
    FindIndexArticlePageListRspVO convertDO2VO(ArticleDO articleDO);

    @Mapping(target = "createMonth", expression = "java(java.time.YearMonth.from(articleDO.getCreateTime()))")
    @Mapping(target = "createDate", expression = "java(java.time.LocalDate.from(articleDO.getCreateTime()))")
    FindArchiveArticleRspVO convertDO2ArchiveVO(ArticleDO articleDO);

    @Mapping(target = "createDate", expression = "java(java.time.LocalDate.from(articleDO.getCreateTime()))")
    FindCategoryOrTagArticlePageListRspVO convertDO2CategoryVO(ArticleDO articleDO);

    @Mapping(target = "createDate", expression = "java(java.time.LocalDate.from(articleDO.getCreateTime()))")
    FindCategoryOrTagArticlePageListRspVO convertDO2TagVO(ArticleDO articleDO);

    @Mapping(target = "articleId", expression = "java(articleDO.getId())")
    @Mapping(target = "articleTitle", expression = "java(articleDO.getTitle())")
    ArticleRspVO convertDO2ArticleVO(ArticleDO articleDO);
}
