package com.abswitch.weblog.common.config;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author：abSwitch
 * @url：
 * @date：2026-03-24 11:11
 * @Description：为什么需要这个接口？
 * 虽然 SQL 注入器已经将方法注入到所有 Mapper 中，但 BaseMapper 接口本身没有声明这个方法，因此直接使用 xxxMapper.insertBatchSomeColumn(...)
 * 会导致编译错误。
 * 通过定义一个继承 BaseMapper 的子接口，并在其中显式声明该方法，业务 Mapper 就可以同时继承 InsertBatchMapper 而获得该方法。
 */
public interface InsertBatchMapper<T> extends BaseMapper<T> {

    // 批量插入
    int insertBatchSomeColumn(@Param("list") List<T> batchList);

}

