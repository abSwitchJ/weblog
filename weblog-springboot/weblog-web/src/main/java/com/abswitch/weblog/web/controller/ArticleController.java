package com.abswitch.weblog.web.controller;

import com.abswitch.weblog.common.aspect.ApiOperationLog;
import com.abswitch.weblog.common.domain.mapper.ArticleMapper;
import com.abswitch.weblog.common.utils.Response;
import com.abswitch.weblog.web.model.vo.article.FindArticleDetailReqVO;
import com.abswitch.weblog.web.service.ArticleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author：abSwitch
 * @url：
 * @date：2026-03-26 17:50
 * @Description：
 */
@RestController
@RequestMapping("/article")
@Tag(name = "文章")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    @PostMapping("/detail")
    @ApiOperationLog(description = "获取文章详情")
    @Operation(summary = "获取文章详情")
    public Response findArticleDetail(@RequestBody FindArticleDetailReqVO findArticleReqDetail){

        return articleService.findArticleDetail(findArticleReqDetail);
    }

}
