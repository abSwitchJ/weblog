package com.abswitch.weblog.common.utils;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Data;

import java.util.List;
import java.util.Objects;

/**
 * @Author：abSwitch
 * @url：
 * @date：2026-03-21 20:05
 * @Description：
 */
@Data
public class PageResponse<T> extends Response<List<T>> {

    /**
     * 总记录数
     */
    private long total = 0L;

    /**
     * 每页显示的记录数，默认每页显示 10 条
     */
    private long size = 10L;

    /**
     * 当前页码
     */
    private long current;

    /**
     * 总页数
     */
    private long pages;

    /**
     * 成功响应
     * @param page Mybatis Plus 提供的分页接口  page当前页
     * @param data
     * @return
     * @param <T>
     */
    public static <T> PageResponse<T> ok(IPage page, List<T> data) {
        PageResponse<T> response = new PageResponse<>();

        response.setSuccess(true);

        boolean pageNull = Objects.isNull(page);

        response.setCurrent(pageNull ? 1L : page.getCurrent());
        response.setSize(pageNull ? 10L : page.getSize());
        response.setPages(pageNull ? 0L : page.getPages());
        response.setTotal(pageNull ? 0L : page.getTotal());
        response.setData(data);
        return response;
    }


    public static <T> PageResponse<T> ok(long total, long current, long size, List<T> data) {
        PageResponse<T> response = new PageResponse<>();
        response.setSuccess(true);
        response.setCurrent(current);
        response.setSize(size);
        // 计算总页数
        int pages = (int) Math.ceil((double) total / size);
        response.setPages(pages);
        response.setTotal(total);
        response.setData(data);
        return response;
    }
}

