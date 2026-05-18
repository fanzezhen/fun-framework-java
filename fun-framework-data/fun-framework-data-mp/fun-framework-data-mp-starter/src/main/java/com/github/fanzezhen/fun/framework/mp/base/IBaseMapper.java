package com.github.fanzezhen.fun.framework.mp.base;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.fanzezhen.fun.framework.core.model.entity.IGenericEntity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * 基础 Mapper 接口
 * <p>
 * 扩展 MyBatis-Plus 的 BaseMapper，提供更多常用的数据库操作方法。
 * 主要功能：
 * <ul>
 *   <li>单字段查询 - 根据单个字段查询、统计、删除</li>
 *   <li>批量操作 - 批量插入、批量更新、批量删除</li>
 *   <li>分页查询 - 支持自定义条件的分页查询</li>
 *   <li>存在性检查 - 检查记录是否存在</li>
 *   <li>保存操作 - 根据主键自动判断是插入还是更新</li>
 * </ul>
 *
 * @param <T> 实体类型，必须实现 IGenericEntity 接口
 */
@SuppressWarnings({"unchecked", "unused"})
public interface IBaseMapper<T extends IGenericEntity<? extends Serializable>> extends com.baomidou.mybatisplus.core.mapper.BaseMapper<T> {
    /**
     * 批量插入时单次插入的行数
     * <p>
     * 为避免单次插入数据量过大导致数据库压力，将大批量数据分批插入，每批次插入 1000 条。
     */
    int INSERT_FETCH_SIZE = 1000;

    /**
     * 根据唯一字段查询单条记录
     *
     * @param column 字段 Lambda 表达式
     * @param value 字段值
     * @return 查询结果，不存在则返回 null
     */
    default T get(final SFunction<T, ?> column, final Object value) {
        LambdaQueryWrapper<T> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(column, value);
        return this.selectOne(lambdaQueryWrapper);
    }

    /**
     * 分页查询
     *
     * @param currentPage 当前页码，从 1 开始
     * @param pageSize 每页记录数
     * @param lambdaQueryWrapper Lambda 查询条件包装器
     * @return 分页结果
     */
    default Page<T> page(final long currentPage, final long pageSize, final LambdaQueryWrapper<T> lambdaQueryWrapper) {
        return this.selectPage(new Page<>(currentPage, pageSize), lambdaQueryWrapper);
    }

    /**
     * 分页查询（查询全部记录）
     *
     * @param currentPage 当前页码，从 1 开始
     * @param pageSize 每页记录数
     * @return 分页结果
     */
    default Page<T> page(final long currentPage, final long pageSize) {
        return page(currentPage, pageSize, Wrappers.lambdaQuery());
    }

    /**
     * 查询全部记录
     *
     * @return 所有记录列表
     */
    default List<T> list() {
        return this.selectList(Wrappers.emptyWrapper());
    }

    /**
     * 根据主键集合查询记录列表
     *
     * @param pks 主键集合，为 null 时返回空列表，为空集合时返回空列表，有元素时按主键筛选
     * @return 查询结果列表
     */
    default List<T> list(final Collection<? extends Serializable> pks) {
        if (CollUtil.isEmpty(pks)) {
            return Collections.emptyList();
        }
        return selectByIds(pks);
    }

    /**
     * 根据字段值检查记录是否存在
     *
     * @param column 字段 Lambda 表达式
     * @param value 字段值，为 null 时检查字段为 NULL 的记录
     * @return true 存在，false 不存在
     */
    default boolean existByColumn(final SFunction<T, ?> column, final Serializable value) {
        LambdaQueryWrapper<T> wrapper = Wrappers.lambdaQuery();
        if (value == null) {
            wrapper.isNull(column);
        } else {
            wrapper.eq(column, value);
        }
        return exists(wrapper);
    }

    /**
     * 根据字段值集合检查记录是否存在
     *
     * @param column 字段 Lambda 表达式
     * @param values 字段值集合
     * @return true 存在，false 不存在
     */
    default boolean existByColumn(final SFunction<T, ?> column, final Collection<? extends Serializable> values) {
        if (CollUtil.isEmpty(values)) {
            return false;
        }
        LambdaQueryWrapper<T> wrapper = Wrappers.lambdaQuery();
        wrapper.in(column, values);
        return exists(wrapper);
    }

    /**
     * 根据字段值统计记录数
     *
     * @param column 字段 Lambda 表达式
     * @param value 字段值，为 null 时统计字段为 NULL 的记录
     * @return 记录数量
     */
    default long countByColumn(final SFunction<T, ?> column, final Serializable value) {
        LambdaQueryWrapper<T> wrapper = Wrappers.lambdaQuery();
        if (value == null) {
            wrapper.isNull(column);
        } else {
            wrapper.eq(column, value);
        }
        return selectCount(wrapper);
    }

    /**
     * 根据字段值集合统计记录数
     *
     * @param column 字段 Lambda 表达式
     * @param values 字段值集合
     * @return 记录数量
     */
    default long countByColumn(final SFunction<T, ?> column, final Collection<? extends Serializable> values) {
        if (CollUtil.isEmpty(values)) {
            return 0L;
        }
        LambdaQueryWrapper<T> wrapper = Wrappers.lambdaQuery();
        wrapper.in(column, values);
        return selectCount(wrapper);
    }

    /**
     * 根据字段值查询记录列表
     *
     * @param column 字段 Lambda 表达式
     * @param value 字段值，为 null 时查询字段为 NULL 的记录
     * @return 查询结果列表
     */
    default List<T> listByColumn(final SFunction<T, ?> column, final Serializable value) {
        LambdaQueryWrapper<T> wrapper = Wrappers.lambdaQuery();
        if (value == null) {
            wrapper.isNull(column);
        } else {
            wrapper.eq(column, value);
        }
        return selectList(wrapper);
    }

    /**
     * 根据字段值集合查询记录列表
     *
     * @param column 字段 Lambda 表达式
     * @param values 字段值集合
     * @return 查询结果列表
     */
    default List<T> listByColumn(final SFunction<T, ?> column, final Collection<? extends Serializable> values) {
        if (CollUtil.isEmpty(values)) {
            return Collections.emptyList();
        }
        LambdaQueryWrapper<T> wrapper = Wrappers.lambdaQuery();
        wrapper.in(column, values);
        return selectList(wrapper);
    }

    /**
     * 根据字段值和主键集合查询记录列表
     *
     * @param column 字段 Lambda 表达式
     * @param value 字段值，为 null 时查询字段为 NULL 的记录
     * @param pks 主键集合，为 null 时不限制主键范围，为空集合时返回空列表
     * @return 查询结果列表
     */
    default List<T> listByColumn(final SFunction<T, ?> column,
                                 final Serializable value,
                                 final Collection<? extends Serializable> pks) {
        LambdaQueryWrapper<T> wrapper = Wrappers.lambdaQuery();
        if (pks != null) {
            if (pks.isEmpty()) {
                return Collections.emptyList();
            }
            wrapper.in(T::getId, pks);
        }
        if (value == null) {
            wrapper.isNull(column);
        } else {
            wrapper.eq(column, value);
        }
        return selectList(wrapper);
    }

    /**
     * 根据字段值集合和主键集合查询记录列表
     *
     * @param column 字段 Lambda 表达式
     * @param values 字段值集合
     * @param pks 主键集合，为 null 时不限制主键范围，为空集合时返回空列表
     * @return 查询结果列表
     */
    default List<T> listByColumn(final SFunction<T, ?> column,
                                 final Collection<? extends Serializable> values,
                                 final Collection<? extends Serializable> pks) {
        if (CollUtil.isEmpty(values)) {
            return Collections.emptyList();
        }
        LambdaQueryWrapper<T> wrapper = Wrappers.lambdaQuery();
        if (pks != null) {
            if (pks.isEmpty()) {
                return Collections.emptyList();
            }
            wrapper.in(T::getId, pks);
        }
        wrapper.in(column, values);
        return selectList(wrapper);
    }

    /**
     * 批量插入记录（排除 UPDATE 填充字段）
     * <p>
     * 由 SQL 注入器提供，批量插入时会排除标记为 UPDATE 填充策略的字段。
     *
     * @param relations 实体列表
     * @return 插入的记录数
     */
    int insertBatchSomeColumn(List<T> relations);

    /**
     * 保存单条记录
     * <p>
     * 根据主键自动判断是插入还是更新：
     * <ul>
     *   <li>主键为 null - 执行插入操作</li>
     *   <li>主键不为 null - 执行更新操作</li>
     * </ul>
     *
     * @param entity 实体对象
     * @param <P> 主键类型
     * @return 主键值
     */
    default <P extends Serializable> P save(final T entity) {
        if (entity == null) {
            return null;
        }
        beforeSave(entity);
        if (entity.getId() == null) {
            insert(entity);
        } else {
            updateById(entity);
        }
        return (P) entity.getId();
    }

    /**
     * 批量保存记录
     * <p>
     * 根据主键自动判断每条记录是插入还是更新，支持大批量数据保存（自动分批）。
     *
     * @param entities 实体集合
     * @return 成功保存的记录数
     */
    default int save(final Collection<T> entities) {
        if (entities == null) {
            return 0;
        }
        beforeSave(entities);
        ArrayList<T> toInsertList = new ArrayList<>(entities.size());
        ArrayList<T> toUpdateList = new ArrayList<>(entities.size());
        for (T entity : entities) {
            if (entity.getId() == null) {
                toInsertList.add(entity);
            } else {
                toUpdateList.add(entity);
            }
        }
        int count = 0;
        if (!toInsertList.isEmpty()) {
            List<List<T>> fetchList = CollUtil.split(toInsertList, INSERT_FETCH_SIZE);
            count += fetchList.stream().mapToInt(this::insertBatchSomeColumn).sum();
        }
        if (!toUpdateList.isEmpty()) {
            count += toUpdateList.stream().mapToInt(this::updateById).sum();
        }
        return count;
    }

    /**
     * 更新单条记录的单个字段
     *
     * @param column 字段 Lambda 表达式
     * @param value 新值
     * @param id 主键
     * @return 更新的记录数
     */
    default int updateColumn(final SFunction<T, ?> column, final Serializable value, final Serializable id) {
        LambdaUpdateWrapper<T> updateWrapper = Wrappers.lambdaUpdate();
        updateWrapper.set(column, value).eq(T::getId, id);
        return update(updateWrapper);
    }

    /**
     * 批量更新记录的单个字段
     *
     * @param column 字段 Lambda 表达式
     * @param value 新值
     * @param pks 主键集合
     * @return 更新的记录数
     */
    default int updateColumn(final SFunction<T, ?> column,
                             final Serializable value,
                             final Collection<? extends Serializable> pks) {
        LambdaUpdateWrapper<T> updateWrapper = Wrappers.lambdaUpdate();
        updateWrapper.set(column, value).in(T::getId, pks);
        return update(updateWrapper);
    }

    /**
     * 根据字段值删除记录
     *
     * @param column 字段 Lambda 表达式
     * @param value 字段值，为 null 时删除字段为 NULL 的记录
     * @return 删除的记录数
     */
    default int del(final SFunction<T, ?> column, final Serializable value) {
        LambdaQueryWrapper<T> wrapper = Wrappers.lambdaQuery();
        if (value == null) {
            wrapper.isNull(column);
        } else {
            wrapper.eq(column, value);
        }
        return delete(wrapper);
    }

    /**
     * 根据字段值集合删除记录
     *
     * @param column 字段 Lambda 表达式
     * @param values 字段值集合，为 null 时删除字段为 NULL 的记录
     * @return 删除的记录数
     */
    default int del(final SFunction<T, ?> column, final Collection<? extends Serializable> values) {
        LambdaQueryWrapper<T> wrapper = Wrappers.lambdaQuery();
        if (values == null) {
            wrapper.isNull(column);
        } else {
            if (values.isEmpty()) {
                return 0;
            }
            wrapper.in(column, values);
        }
        return delete(wrapper);
    }

    /**
     * 保存单条记录前的钩子方法
     * <p>
     * 子类可以重写此方法，在保存前对实体进行额外处理。
     *
     * @param entity 待保存的实体
     */
    default void beforeSave(final T entity) {
    }

    /**
     * 批量保存记录前的钩子方法
     * <p>
     * 子类可以重写此方法，在保存前对实体集合进行额外处理。
     *
     * @param entities 待保存的实体集合
     */
    default void beforeSave(final Collection<T> entities) {
    }

}
