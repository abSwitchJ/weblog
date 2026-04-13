package com.abswitch.weblog.web.convert;

import com.abswitch.weblog.common.domain.dos.CategoryDO;
import com.abswitch.weblog.web.model.vo.FindCategoryOrTagListRspVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @Author：abSwitch
 * @url：
 * @date：2026-03-26 10:31
 * @Description：
 */
@Mapper
public interface CategoryConvert {

    CategoryConvert INSTANCE = Mappers.getMapper(CategoryConvert.class);


    FindCategoryOrTagListRspVO convertDO2VO(CategoryDO categoryDO);
}
