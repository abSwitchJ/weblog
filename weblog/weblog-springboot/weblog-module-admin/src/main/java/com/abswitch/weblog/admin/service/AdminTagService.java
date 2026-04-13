package com.abswitch.weblog.admin.service;

import com.abswitch.weblog.admin.model.vo.tag.AddTagsReqVO;
import com.abswitch.weblog.admin.model.vo.tag.DeleteTagReqVO;
import com.abswitch.weblog.admin.model.vo.tag.FindTagPageListReqVO;
import com.abswitch.weblog.common.utils.PageResponse;
import com.abswitch.weblog.common.utils.Response;

/**
 * @Author：abSwitch
 * @url：
 * @date：2026-03-22 14:20
 * @Description：
 */
public interface AdminTagService {

    Response addTags(AddTagsReqVO addTagsReqVO);

    PageResponse findTagList(FindTagPageListReqVO findTagPageListReqVO);

    Response deleteTag(DeleteTagReqVO deleteTagReqVO);

    Response findTagSelectList();


}
