package com.abswitch.weblog.web.convert;

import com.abswitch.weblog.common.domain.dos.TagDO;
import com.abswitch.weblog.web.model.vo.tag.FindTagListRspVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @Author：abSwitch
 * @url：
 * @date：2026-03-26 11:04
 * @Description：
 */
@Mapper
public interface TagConvert {

    TagConvert INSTANCE = Mappers.getMapper(TagConvert.class);

    FindTagListRspVO convertDO2VO(TagDO tagDO);
}
