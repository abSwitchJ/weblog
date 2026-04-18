package com.abswitch.weblog.web.controller;

import com.abswitch.weblog.common.aspect.ApiOperationLog;
import com.abswitch.weblog.common.utils.Response;
import com.abswitch.weblog.web.model.vo.archive.FindArchiveListReqVO;
import com.abswitch.weblog.web.service.ArchiveService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "文章归档")
public class ArchiveController {

    @Autowired
    private ArchiveService archiveService;

    @PostMapping("/archive")
    @ApiOperationLog(description = "获取文章归档数据")
    @Operation(summary = "获取文章归档数据")
    public Response findArchiveList(@RequestBody(required = false) FindArchiveListReqVO reqVO) {
        return archiveService.findArchiveList(reqVO == null ? new FindArchiveListReqVO() : reqVO);
    }
}
