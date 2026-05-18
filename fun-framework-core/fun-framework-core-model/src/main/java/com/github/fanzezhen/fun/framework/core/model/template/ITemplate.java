package com.github.fanzezhen.fun.framework.core.model.template;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.lang.func.Func1;
import cn.hutool.core.lang.func.LambdaUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ReflectUtil;
import com.github.fanzezhen.fun.framework.core.model.annotation.Column;
import com.github.fanzezhen.fun.framework.core.model.annotation.Entity;
import com.github.fanzezhen.fun.framework.core.model.entity.IEntity;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * 操作模板接口
 * <p>
 * 定义数据访问的通用模板方法，包括增删改查等基础操作。
 * 支持基于 Lambda 表达式的字段名解析，简化数据访问代码。
 * </p>
 *
 * @param <P> 主键类型
 */
public interface ITemplate<P extends Serializable> {

    /**
     * 根据指定列查询单个对象
     *
     * @param column 列名
     * @param value  列值
     * @param clz    对象类型
     * @param <T>    返回类型
     * @return 查询结果，未找到返回 null
     */
    <T> T get(String column, Serializable value, Class<T> clz);

    /**
     * 根据主键查询对象
     *
     * @param id  主键值
     * @param clz 对象类型
     * @param <T> 返回类型
     * @return 查询结果，未找到返回 null
     */
    <T> T getById(Serializable id, Class<T> clz);

    /**
     * 根据主键集合批量查询
     *
     * @param ids 主键集合
     * @param clz 对象类型
     * @param <T> 返回类型
     * @return 查询结果列表，未找到返回空列表
     */
    <T> List<T> listByIds(Collection<? extends Serializable> ids, Class<T> clz);

    /**
     * 根据指定列查询对象列表（单值匹配）
     *
     * @param column 列名
     * @param value  列值
     * @param clz    对象类型
     * @param <T>    返回类型
     * @return 查询结果列表
     */
    <T> List<T> listByColumn(String column, Serializable value, Class<T> clz);

    /**
     * 根据指定列查询对象列表（多值匹配）
     *
     * @param column 列名
     * @param values 列值集合
     * @param clz    对象类型
     * @param <T>    返回类型
     * @return 查询结果列表
     */
    <T> List<T> listByColumn(String column, Collection<? extends Serializable> values, Class<T> clz);

    /**
     * 批量插入实体对象
     * <p>
     * 实体类需要使用 @Entity 注解标记，主键字段需要使用 @Column 注解标记
     * </p>
     *
     * @param entities 实体对象集合
     * @return true 表示插入成功，false 表示插入失败
     */
    boolean insert(Collection<IEntity<P>> entities);

    /**
     * 根据主键集合删除数据
     *
     * @param ids 主键集合
     * @param clz 实体类型（需要使用 @Entity 注解标记）
     * @return true 表示删除成功，false 表示删除失败
     */
    boolean deleteById(Collection<P> ids, Class<? extends IEntity<P>> clz);

    /**
     * 根据主键删除数据
     *
     * @param id  主键值
     * @param clz 实体类型（需要使用 @Entity 注解标记）
     * @return true 表示删除成功，false 表示删除失败
     */
    default boolean deleteById(P id, Class<? extends IEntity<P>> clz){
        return deleteById(Collections.singletonList(id), clz);
    }

    /**
     * 插入单个实体对象
     * <p>
     * 实体类需要使用 @Entity 注解标记，主键字段需要使用 @Column 注解标记
     * </p>
     *
     * @param entity 实体对象
     * @return 插入成功后的主键值
     */
    default P insert(IEntity<P> entity){
        insert(Collections.singletonList(entity));
        return entity.getId();
    }

    /**
     * 根据函数式引用查询单个对象
     * <p>
     * 使用 Lambda 表达式引用实体字段，自动解析为数据库列名
     * </p>
     *
     * @param func  字段引用函数
     * @param value 列值
     * @param clz   对象类型
     * @param <T>   返回类型
     * @return 查询结果，未找到返回 null
     */
    default <T> T get(Func1<T, ?> func, Serializable value, Class<T> clz) {
        return get(getColumnName(func), value, clz);
    }

    /**
     * 获取实体类对应的表名
     * <p>
     * 优先使用 @Entity 注解中的 table 和 tablePrefix，
     * 否则将类名转换为下划线格式作为表名
     * </p>
     *
     * @param clz           实体类型
     * @param defaultPrefix 默认表前缀
     * @return 表名，如果 clz 为 null 则返回 null
     */
    static String getTable(Class<?> clz, String defaultPrefix) {
        if (clz == null) {
            return null;
        }
        String table = null;
        String tablePrefix = null;
        Entity entity = AnnotationUtil.getAnnotation(clz, Entity.class);
        if (entity != null) {
            table = entity.table();
            tablePrefix = entity.tablePrefix();
        }
        tablePrefix = CharSequenceUtil.emptyToDefault(tablePrefix, CharSequenceUtil.nullToEmpty(defaultPrefix));
        if (CharSequenceUtil.isEmpty(table)) {
            table = CharSequenceUtil.toUnderlineCase(CharSequenceUtil.lowerFirst(clz.getSimpleName()));
        }
        return tablePrefix + table;
    }

    /**
     * 获取实体类对应的表名（无前缀）
     *
     * @param clz 实体类型
     * @return 表名
     */
    static String getTable(Class<?> clz) {
        return getTable(clz, CharSequenceUtil.EMPTY);
    }

    /**
     * 从函数式引用解析列名
     * <p>
     * 通过 Lambda 表达式引用解析出对应的数据库列名：
     * 1. 优先使用 @Column 注解指定的列名
     * 2. 否则将字段名从驼峰命名转换为下划线命名
     * </p>
     *
     * @param func 字段引用函数（getter 方法或字段引用）
     * @param <T>  实体类型
     * @return 数据库列名
     */
    static <T> String getColumnName(Func1<T, ?> func) {
        Class<T> realClass = LambdaUtil.getRealClass(func);
        String methodName = LambdaUtil.getMethodName(func);
        String fieldName = null;
        Column column = ReflectUtil.getMethod(realClass, methodName).getAnnotation(Column.class);
        if (column == null || CharSequenceUtil.isEmpty(column.name())) {
            fieldName = LambdaUtil.getFieldName(func);
            column = ReflectUtil.getField(realClass, fieldName).getAnnotation(Column.class);
        }
        if (column != null && CharSequenceUtil.isNotEmpty(column.name())) {
            return column.name();
        }
        if (CharSequenceUtil.isNotEmpty(fieldName)) {
            return CharSequenceUtil.toUnderlineCase(fieldName);
        }
        if (methodName.startsWith("get")) {
            return CharSequenceUtil.toUnderlineCase(CharSequenceUtil.lowerFirst(methodName.substring(3)));
        } else if (methodName.startsWith("is")) {
            return CharSequenceUtil.toUnderlineCase(CharSequenceUtil.lowerFirst(methodName.substring(2)));
        }
        return CharSequenceUtil.toUnderlineCase(methodName);
    }

}
