package com.abswitch.weblog.admin.service.impl;

import com.abswitch.weblog.admin.convert.BlogSettingsConvert;
import com.abswitch.weblog.admin.model.vo.blogsettings.FindBlogSettingsRspVO;
import com.abswitch.weblog.admin.model.vo.blogsettings.UpdateBlogSettingsReqVO;
import com.abswitch.weblog.admin.service.AdminBlogSettingsService;
import com.abswitch.weblog.common.domain.dos.BlogSettingsDO;
import com.abswitch.weblog.common.domain.mapper.BlogSettingsMapper;
import com.abswitch.weblog.common.utils.Response;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author：abSwitch
 * @url：
 * @date：2026-03-23 19:34
 * @Description：
 */
@Service
@Slf4j
public class AdminBlogSettingsImpl extends ServiceImpl<BlogSettingsMapper, BlogSettingsDO> implements AdminBlogSettingsService {

    @Autowired
    private BlogSettingsMapper blogSettingsMapper;
    @Autowired
    private BlogSettingsConvert blogSettingsConvert;

    @Override
    public Response updateBlogSettings(UpdateBlogSettingsReqVO updateBlogSettingReqVO) {

        //VO 转 DO
        BlogSettingsDO blogSettingsDO = blogSettingsConvert.convertVO2DO(updateBlogSettingReqVO);
        blogSettingsDO.setId(1L);


        // 保存或更新（当数据库中存在 ID 为 1 的记录时，则执行更新操作，否则执行插入操作）
        saveOrUpdate(blogSettingsDO);

        return Response.ok();
    }

    @Override
    public Response findDetail() {

        FindBlogSettingsRspVO findBlogSettingsRspVO = blogSettingsConvert.convertDO2VO(blogSettingsMapper.selectById(1L));

        return Response.ok(findBlogSettingsRspVO);
    }
}
