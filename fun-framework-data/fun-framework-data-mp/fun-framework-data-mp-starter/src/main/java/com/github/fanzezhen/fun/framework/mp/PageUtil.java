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
 * 提供 MyBatis Plus 的 IPage 与框架统一的 PageDTO 之间的转换功能，
 * 以及基于内存数据的分页处理。
 * <p>
 * 主要功能：
 * <ul>
 *   <li>IPage 转 PageDTO - 将 MyBatis-Plus 分页对象转换为框架分页对象</li>
 *   <li>PageCondition 转 IPage - 将分页查询条件转换为 MyBatis-Plus 分页对象</li>
 *   <li>内存分页 - 对内存中的数据进行分页处理</li>
 * </ul>
 */
public final class PageUtil {
    /**
     * 私有构造函数，防止实例化
     */
    private PageUtil() {}

    /**
     * 将 MyBatis-Plus 分页对象转换为框架分页结果对象
     *
     * @param page MyBatis-Plus 分页对象
     * @param <T> 数据类型
     * @return 框架分页结果对象
     */
    public static <T> PageDTO<T> toPageResult(final IPage<T> page) {
        PageDTO<T> pageResult = new PageDTO<>();
        pageResult.setRecords(page.getRecords());
        pageResult.setCurrent((int) page.getCurrent());
        pageResult.setSize((int) page.getSize());
        pageResult.setTotal(page.getTotal());
        return pageResult;
    }

    /**
     * 根据一页数据和分页信息组装成分页结果
     * <p>
     * 适用于需要对分页数据进行转换的场景，例如 Entity 转 DTO。
     *
     * @param page MyBatis-Plus 分页对象，提供分页元数据（当前页、每页大小、总记录数）
     * @param records 转换后的记录列表
     * @param <R> 结果数据类型
     * @return 框架分页结果对象
     */
    public static <R> PageDTO<R> toPageResult(final IPage<?> page, final List<R> records) {
        PageDTO<R> pageResult = new PageDTO<>();
        pageResult.setRecords(records);
        pageResult.setCurrent((int) page.getCurrent());
        pageResult.setSize((int) page.getSize());
        pageResult.setTotal(page.getTotal());
        return pageResult;
    }

    /**
     * 根据一页数据和分页信息组装成分页结果
     * <p>
     * 适用于 PageDTO 到 PageDTO 的数据转换场景。
     *
     * @param page 源分页对象，提供分页元数据
     * @param records 转换后的记录列表
     * @param <R> 结果数据类型
     * @return 框架分页结果对象
     */
    public static <R> PageDTO<R> toPageResult(final PageDTO<?> page, final List<R> records) {
        PageDTO<R> pageResult = new PageDTO<>();
        pageResult.setRecords(records);
        pageResult.setCurrent(page.getCurrent());
        pageResult.setSize(page.getSize());
        pageResult.setTotal(page.getTotal());
        return pageResult;
    }

    /**
     * 将分页查询条件转换为 MyBatis-Plus 分页查询对象
     * <p>
     * 用于 DAO 层方法中，将框架统一的分页条件转换为 MyBatis-Plus 可识别的 Page 对象。
     *
     * @param condition 框架分页查询条件
     * @param <T> 数据类型
     * @return MyBatis-Plus 分页查询对象
     */
    public static <T> IPage<T> toPage(final PageCondition condition) {
        return new Page<>(condition.getCurrent(), condition.getSize());
    }

    /**
     * 通过分页参数构造 MyBatis-Plus 分页查询对象
     *
     * @param currentPage 当前页码，从 1 开始
     * @param pageSize 每页记录数
     * @param <T> 数据类型
     * @return MyBatis-Plus 分页查询对象
     */
    public static <T> IPage<T> toPage(final Long currentPage, final Long pageSize) {
        return new Page<>(currentPage, pageSize);
    }


    /**
     * 对内存数据进行分页查询
     * <p>
     * 适用于需要对已加载到内存中的数据进行分页展示的场景。
     *
     * @param currentPage 当前页码，从 1 开始
     * @param pageSize 每页记录数
     * @param pageData 所有数据列表
     * @param <T> 数据类型
     * @return 框架分页结果对象
     */
    public static <T> PageDTO<T> pageResult(final Long currentPage, final Long pageSize, final List<T> pageData) {
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
