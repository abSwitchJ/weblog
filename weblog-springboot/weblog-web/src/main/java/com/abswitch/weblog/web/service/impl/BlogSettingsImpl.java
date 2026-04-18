package com.abswitch.weblog.web.service.impl;

import com.abswitch.weblog.common.domain.dos.BlogSettingsDO;
import com.abswitch.weblog.common.domain.mapper.BlogSettingsMapper;
import com.abswitch.weblog.common.utils.Response;
import com.abswitch.weblog.web.convert.BlogSettingsConvert;
import com.abswitch.weblog.web.markdown.MarkdownHelper;
import com.abswitch.weblog.web.model.vo.blogsettings.FindAboutDetailRspVO;
import com.abswitch.weblog.web.model.vo.blogsettings.FindBlogSettingsDetailRspVO;
import com.abswitch.weblog.web.service.BlogSettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author：abSwitch
 * @url：
 * @date：2026-03-26 11:23
 * @Description：
 */
@Service
public class BlogSettingsImpl implements BlogSettingsService {

    @Autowired
    private BlogSettingsMapper blogSettingsMapper;

    @Override
    public Response findDetail() {
        FindBlogSettingsDetailRspVO findBlogSettingsDetailRspVO = BlogSettingsConvert.INSTANCE
                .convertDO2VO(blogSettingsMapper.selectById(1L));

        return Response.ok(findBlogSettingsDetailRspVO);
    }

    @Override
    public Response findAboutDetail() {
        BlogSettingsDO blogSettingsDO = blogSettingsMapper.selectById(1L);
        String about = blogSettingsDO == null ? null : blogSettingsDO.getAbout();
        String aboutHtml = (about == null || about.isEmpty())
                ? ""
                : MarkdownHelper.convertMarkdown2Html(about);

        return Response.ok(FindAboutDetailRspVO.builder().aboutHtml(aboutHtml).build());
    }
}
