package com.abswitch.weblog.admin.convert;

import com.abswitch.weblog.admin.model.vo.article.FindArticleDetailRspVO;
import com.abswitch.weblog.admin.model.vo.article.FindArticlePageListRspVO;
import com.abswitch.weblog.admin.model.vo.article.PublishArticleReqVO;
import com.abswitch.weblog.admin.model.vo.article.UpdateArticleReqVO;
import com.abswitch.weblog.common.domain.dos.ArticleContentDO;
import com.abswitch.weblog.common.domain.dos.ArticleDO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * @Author：abSwitch
 * @url：
 * @date：2026-03-23 22:20
 * @Description：
 */
//使用 componentModel = "spring" 可以自动生成 Spring Bean
@Mapper(componentModel = "spring")
public interface ArticleConvert {
    ArticleDO convertVO2DO(PublishArticleReqVO vo);
    ArticleContentDO convertVO2ContentDO(PublishArticleReqVO vo);

    @Mapping(target = "isTop", expression = "java(articleDO.getWeight()>0)")
    FindArticlePageListRspVO convertArticleDO2VO(ArticleDO articleDO);

    /**
     * 将 DO 转化为 VO
     * @param bean
     * @return
     */
    FindArticleDetailRspVO convertDO2VO(ArticleDO bean);

    ArticleDO convertUpdateVO2DO(UpdateArticleReqVO bean);
    ArticleContentDO convertUpdateVO2ContentDO(UpdateArticleReqVO bean);
}