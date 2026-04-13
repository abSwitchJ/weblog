package com.abswitch.weblog.common.domain.mapper;

import com.abswitch.weblog.common.domain.dos.CategoryDO;
import com.abswitch.weblog.common.domain.dos.TagDO;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author：abSwitch
 * @url：
 * @date：2026-03-22 14:31
 * @Description：
 */
public interface TagMapper extends BaseMapper<TagDO> {

    default TagDO selectByName(String tagName){

        LambdaUpdateWrapper<TagDO> wrapper = new LambdaUpdateWrapper<>();

        wrapper.eq(TagDO::getName, tagName);

        return selectOne(wrapper);
    }
    int insertBatch(@Param("list") List<TagDO> tagList);

    /**
     * 根据标签 ID 批量查询
     * @param tagIds
     * @return
     */
    default List<TagDO> selectByIds(List<Long> tagIds) {
        return selectList(Wrappers.<TagDO>lambdaQuery()
                .in(TagDO::getId, tagIds));
    }

    /**
     * 查询时指定数量
     * @param limit
     * @return
     */
    default List<TagDO> selectByLimit(Long limit) {
        return selectList(Wrappers.<TagDO>lambdaQuery()
                .orderByDesc(TagDO::getArticlesTotal) // 根据文章总数降序
                .last(String.format("LIMIT %d", limit))); // 查询指定数量
    }
}
