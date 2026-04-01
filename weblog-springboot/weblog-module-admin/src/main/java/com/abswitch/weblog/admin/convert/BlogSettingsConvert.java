package com.abswitch.weblog.admin.convert;

import com.abswitch.weblog.admin.model.vo.blogsettings.FindBlogSettingsRspVO;
import com.abswitch.weblog.admin.model.vo.blogsettings.UpdateBlogSettingsReqVO;
import com.abswitch.weblog.common.domain.dos.BlogSettingsDO;
import org.mapstruct.Mapper;

/**
 * @Author：abSwitch
 * @url：
 * @date：2026-03-23 20:05
 * @Description：
 */
@Mapper(componentModel = "spring")
public interface BlogSettingsConvert {

    /**
     * 将 VO 转化为 DO
     * @param bean
     * @return
     */
    BlogSettingsDO convertVO2DO(UpdateBlogSettingsReqVO bean);

    /**
     * 将 DO 转化为 VO
     * @param bean
     * @return
     */
    FindBlogSettingsRspVO convertDO2VO(BlogSettingsDO bean);

}
