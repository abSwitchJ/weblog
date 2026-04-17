package com.abswitch.weblog.web.service;

import com.abswitch.weblog.common.utils.Response;
import com.abswitch.weblog.web.model.vo.archive.FindArchiveListReqVO;

public interface ArchiveService {
    Response findArchiveList(FindArchiveListReqVO reqVO);
}
