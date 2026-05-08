package com.github.fanzezhen.fun.framework.mp;

import cn.hutool.core.collection.ListUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.fanzezhen.fun.framework.core.model.condition.PageCondition;
import com.github.fanzezhen.fun.framework.core.model.dto.PageDTO;

import java.util.List;

/**
 * 分页工具类
 * <p>
 * 提供MyBatis Plus的IPage与框架统一的PageDTO之间的转换功能，
 * 以及基于内存数据的分页处理。
 *
 */
public final class PageUtil {
    /**
     * 构造函数
     */
    private PageUtil() {}

    /**
     * mybatis分页对象转换为pageResult
     */
    public static <T> PageDTO<T> toPageResult(IPage<T> page) {
        PageDTO<T> pageResult = new PageDTO<>();
        pageResult.setRecords(page.getRecords());
        pageResult.setCurrent((int) page.getCurrent());
        pageResult.setSize((int) page.getSize());
        pageResult.setTotal(page.getTotal());
        return pageResult;
    }

    /**
     * 根据一页数据和分页信息组装成分页结果
     * 
     * @param page mybatis分页对象
     * @param records 一页的部分记录
     * @return 分页结果
     * @param <R> 结果泛型
     */
    public static <R> PageDTO<R> toPageResult(IPage<?> page, List<R> records) {
        PageDTO<R> pageResult = new PageDTO<>();
        pageResult.setRecords(records);
        pageResult.setCurrent((int) page.getCurrent());
        pageResult.setSize((int) page.getSize());
        pageResult.setTotal(page.getTotal());
        return pageResult;
    }

    /**
     * 根据一页数据和分页信息组装成分页结果
     *
     * @param page mybatis分页对象
     * @param records 一页的部分记录
     * @return 分页结果
     * @param <R> 结果泛型
     */
    public static <R> PageDTO<R> toPageResult(PageDTO<?> page, List<R> records) {
        PageDTO<R> pageResult = new PageDTO<>();
        pageResult.setRecords(records);
        pageResult.setCurrent(page.getCurrent());
        pageResult.setSize(page.getSize());
        pageResult.setTotal(page.getTotal());
        return pageResult;
    }

    /**
     * condition 转 IPage查询对象
     * 
     * @param condition 分页查询条件
     * @return mybatisIPage查询对象
     */
    public static <T> IPage<T> toPage(PageCondition condition) {
        return new Page<>(condition.getCurrent(), condition.getSize());
    }

    /**
     * 通过分页参数狗仔 IPage查询对象
     * 
     * @param currentPage 第几页
     * @param pageSize 每页数量
     * @return IPage
     */
    public static <T> IPage<T> toPage(Long currentPage, Long pageSize) {
        return new Page<>(currentPage, pageSize);
    }


    /**
     * 分页查询
     */
    public static <T> PageDTO<T> pageResult(Long currentPage, Long pageSize, List<T> pageData) {
        PageDTO<T> pageResult = new PageDTO<>();
        List<T> rows = ListUtil.page(currentPage.intValue() - 1, pageSize.intValue(), pageData);
        pageResult.setRecords(rows);
        pageResult.setCurrent(Math.toIntExact(currentPage));
        pageResult.setSize(Math.toIntExact(pageSize));
        pageResult.setTotal((long) pageData.size());
        pageResult.setTotalTime(null);
        return pageResult;
    }

}
