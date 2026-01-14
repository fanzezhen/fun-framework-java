package com.github.fanzezhen.fun.framework.core.data.template;

import cn.hutool.core.lang.func.Func1;
import cn.hutool.core.lang.func.LambdaUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ReflectUtil;
import com.github.fanzezhen.fun.framework.core.data.annotation.Column;
import com.github.fanzezhen.fun.framework.core.data.annotation.Entity;
import com.github.fanzezhen.fun.framework.core.model.entity.IEntity;
import org.springframework.core.annotation.AnnotationUtils;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * 操作模板接口
 */
public interface ITemplate<P extends Serializable> {

    /**
     * 根据唯一字段查询
     */
    <T> T get(String column, Serializable value, Class<T> clz);

    /**
     * 通过id查询文档
     *
     * @param id  主键
     * @param clz 文档类型
     * @param <T> 返回值类型
     *
     * @return 查询文档
     */
    <T> T getById(Serializable id, Class<T> clz);

    /**
     * mGet方法，批量构建 get 请求
     * <p>
     * co.elastic.clients.elasticsearch.core.MGetRequest.Builder 作为入参
     *
     * @param ids 查询条件，索引名优先使用@Document中的
     * @param clz 文档类型
     * @param <T> 返回文档类型
     *
     * @return 查询结果列表
     */
    <T> List<T> listByIds(Collection<? extends Serializable> ids, Class<T> clz);

    /**
     * 根据唯一字段查询
     */
    <T> List<T> listByColumn(String column, Serializable value, Class<T> clz);

    /**
     * 根据唯一字段查询
     */
    <T> List<T> listByColumn(String column, Collection<? extends Serializable> values, Class<T> clz);
    /**
     * 插入文档
     *
     * @param entities 文档对象
     *               类需要被 {@link Entity} 标记
     *               字段需要被 {@link Column} 标记出主键
     */
    boolean insert(Collection<IEntity<P>> entities);

    /**
     * 按条件删除索引数据
     *
     * @param ids 需要删除的id
     * @param clz 类需要被 {@link Entity} 标记
     *
     * @return 删除条数
     */
    boolean deleteById(Collection<P> ids, Class<? extends IEntity<P>> clz);

    /**
     * 按条件删除索引数据
     *
     * @param id 需要删除的id
     * @param clz 类需要被 {@link Entity} 标记
     *
     * @return 删除条数
     */
    default boolean deleteById(P id, Class<? extends IEntity<P>> clz){
        return deleteById(Collections.singletonList(id), clz);
    }

    /**
     * 插入文档
     *
     * @param entity 文档对象
     *               类需要被 {@link Entity} 标记
     *               字段需要被 {@link Column} 标记出主键
     */
    default P insert(IEntity<P> entity){
        insert(Collections.singletonList(entity));
        return entity.getId();
    }
    
    /**
     * 根据唯一字段查询
     */
    default <T> T get(Func1<T, ?> func, Serializable value, Class<T> clz) {
        return get(getColumnName(func), value, clz);
    }

    static String getTable(Class<?> clz, String defaultPrefix) {
        if (clz == null) {
            return null;
        }
        String table = null;
        String tablePrefix = null;
        Entity entity = AnnotationUtils.getAnnotation(clz, Entity.class);
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

    static String getTable(Class<?> clz) {
        return getTable(clz, CharSequenceUtil.EMPTY);
    }

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
