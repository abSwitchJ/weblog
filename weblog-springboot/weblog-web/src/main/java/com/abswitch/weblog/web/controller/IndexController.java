package com.abswitch.weblog.web.controller;

import com.abswitch.weblog.common.aspect.ApiOperationLog;
import com.abswitch.weblog.common.utils.Response;
import com.abswitch.weblog.web.model.vo.FindIndexArticleOrArchivePageListReqVO;
import com.abswitch.weblog.web.service.ArticleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author：abSwitch
 * @url：
 * @date：2026-03-25 13:56
 * @Description：
 */
@RestController
@Tag(name = "文章")
public class IndexController {

    @Autowired
    private ArticleService articleService;



    @PostMapping("/article/list")
    @ApiOperationLog(description = "获取首页文章分页数据")
    @Operation(summary = "获取首页文章分页数据")
    public Response findArticlePageList(@RequestBody FindIndexArticleOrArchivePageListReqVO findIndexArticlePageListReqVO) {
        return articleService.findArticlePageList(findIndexArticlePageListReqVO);
    }


}
