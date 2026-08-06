package com.github.fanzezhen.fun.framework.data.graph.base.mapping;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ReflectUtil;
import com.github.fanzezhen.fun.framework.core.model.enums.FunCoreDataExceptionEnum;
import com.github.fanzezhen.fun.framework.core.model.exception.ServiceException;
import com.github.fanzezhen.fun.framework.data.graph.base.model.GraphNodeData;
import com.github.fanzezhen.fun.framework.data.graph.base.model.GraphRecordData;
import com.github.fanzezhen.fun.framework.data.graph.base.model.GraphRelationshipData;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 默认图结果映射引擎
 * <p>
 * 基于注解元数据与反射完成映射。设计取向是"缺失与不可转换都不致命"：
 * 单个属性转换失败只跳过该字段并记 debug 日志，不让整行映射失败，
 * 因为图查询的返回列常随语句变化，过严会让调用方被迫为每种投影建专用类型。
 * </p>
 *
 * @since 4.1.1
 */
@Slf4j
public class DefaultGraphMapper implements IGraphMapper {

    @Override
    public <T> T mapOne(final GraphRecordData recordData, final Class<T> clz) {
        if (recordData == null || recordData.isEmpty() || clz == null) {
            return null;
        }
        final GraphEntityMeta meta = GraphClassCache.getMeta(clz);
        if (meta.isGraphEntity()) {
            final Object element = findMatchedElement(recordData, meta);
            if (element != null) {
                return mapElement(element, clz);
            }
        }
        return mapColumns(recordData, clz, meta);
    }

    @Override
    public <T> List<T> mapList(final List<GraphRecordData> recordList, final Class<T> clz) {
        if (CollUtil.isEmpty(recordList) || clz == null) {
            return Collections.emptyList();
        }
        final List<T> resultList = new ArrayList<>(recordList.size());
        for (final GraphRecordData recordData : recordList) {
            final T mapped = mapOne(recordData, clz);
            if (mapped != null) {
                resultList.add(mapped);
            }
        }
        return resultList;
    }

    @Override
    public <T> T mapObject(final GraphRecordData recordData, final Class<T> clz) {
        if (recordData == null || recordData.isEmpty() || clz == null) {
            return null;
        }
        return convertValue(recordData.firstValue(), clz, clz);
    }

    /**
     * 在结果行中查找与目标图实体匹配的节点或关系列
     * <p>
     * 节点实体找 {@link GraphNodeData} 列，关系实体找 {@link GraphRelationshipData} 列。
     * 找不到则返回 null，交由投影映射处理。
     * </p>
     *
     * @param recordData 结果行
     * @param meta   目标类型元数据
     * @return 匹配到的中间表示，无匹配返回 null
     */
    private static Object findMatchedElement(final GraphRecordData recordData, final GraphEntityMeta meta) {
        final Class<?> expectedType = meta.isNodeEntity() ? GraphNodeData.class : GraphRelationshipData.class;
        for (final Object value : recordData.columns().values()) {
            if (expectedType.isInstance(value)) {
                return value;
            }
        }
        return null;
    }

    /**
     * 按列名映射结果行到目标对象
     *
     * @param recordData 结果行
     * @param clz    目标类型
     * @param meta   目标类型元数据
     * @param <T>    目标类型泛型
     * @return 映射结果
     */
    private <T> T mapColumns(final GraphRecordData recordData, final Class<T> clz, final GraphEntityMeta meta) {
        final T bean = newInstance(clz);
        recordData.columns().forEach((column, value) -> {
            for (final Field field : meta.getFields(column)) {
                setFieldValue(bean, field, value);
            }
        });
        return bean;
    }

    /**
     * 映射节点或关系中间表示到目标对象
     *
     * @param element 中间表示
     * @param clz     目标类型
     * @param <T>     目标类型泛型
     * @return 映射结果
     */
    private <T> T mapElement(final Object element, final Class<T> clz) {
        if (element instanceof GraphNodeData nodeData) {
            return mapNode(nodeData, clz);
        }
        if (element instanceof GraphRelationshipData relationshipData) {
            return mapRelationship(relationshipData, clz);
        }
        return convertValue(element, clz, clz);
    }

    /**
     * 映射节点中间表示到目标对象
     *
     * @param nodeData 节点中间表示
     * @param clz      目标类型
     * @param <T>      目标类型泛型
     * @return 映射结果
     */
    private <T> T mapNode(final GraphNodeData nodeData, final Class<T> clz) {
        final Class<T> actualClass = resolveActualClass(clz, GraphClassCache.findNodeClass(firstLabel(nodeData)));
        final GraphEntityMeta meta = GraphClassCache.getMeta(actualClass);
        final T bean = newInstance(actualClass);
        setSpecialField(bean, meta.getElementIdField(), nodeData.getElementId());
        setSpecialField(bean, meta.getLabelsField(), nodeData.getLabelsOrEmpty());
        applyProperties(bean, meta, nodeData.getPropertiesOrEmpty());
        return bean;
    }

    /**
     * 映射关系中间表示到目标对象
     *
     * @param relationshipData 关系中间表示
     * @param clz              目标类型
     * @param <T>              目标类型泛型
     * @return 映射结果
     */
    private <T> T mapRelationship(final GraphRelationshipData relationshipData, final Class<T> clz) {
        final Class<T> actualClass =
            resolveActualClass(clz, GraphClassCache.findRelationshipClass(relationshipData.getType()));
        final GraphEntityMeta meta = GraphClassCache.getMeta(actualClass);
        final T bean = newInstance(actualClass);
        setSpecialField(bean, meta.getElementIdField(), relationshipData.getElementId());
        setSpecialField(bean, meta.getTypeField(), relationshipData.getType());
        mapEndpoint(bean, meta.getStartNodeField(), relationshipData.getStartNode());
        mapEndpoint(bean, meta.getEndNodeField(), relationshipData.getEndNode());
        applyProperties(bean, meta, relationshipData.getPropertiesOrEmpty());
        return bean;
    }

    /**
     * 映射关系的起止节点字段
     *
     * @param bean     关系对象
     * @param field    起止节点字段
     * @param nodeData 节点中间表示
     */
    private void mapEndpoint(final Object bean, final Field field, final GraphNodeData nodeData) {
        if (field == null || nodeData == null) {
            return;
        }
        setSpecialField(bean, field, mapNode(nodeData, field.getType()));
    }

    /**
     * 把属性映射写入对象字段
     *
     * @param bean       目标对象
     * @param meta       目标类型元数据
     * @param properties 属性映射
     */
    private void applyProperties(final Object bean, final GraphEntityMeta meta, final Map<String, Object> properties) {
        properties.forEach((propertyName, value) -> {
            for (final Field field : meta.getFields(propertyName)) {
                setFieldValue(bean, field, value);
            }
        });
    }

    /**
     * 设置字段值，按字段声明类型做转换
     * <p>
     * 字段为节点/关系实体或其集合时递归映射，否则交由 hutool 做类型转换。
     * 单个字段转换失败只跳过该字段。
     * </p>
     *
     * @param bean  目标对象
     * @param field 字段
     * @param value 原始值
     */
    private void setFieldValue(final Object bean, final Field field, final Object value) {
        if (field == null || value == null) {
            return;
        }
        final Object fieldValue = convertValue(value, field.getType(), field.getGenericType());
        if (fieldValue != null) {
            ReflectUtil.setFieldValue(bean, field, fieldValue);
        }
    }

    /**
     * 直接设置字段值，不做类型转换以外的处理
     *
     * @param bean  目标对象
     * @param field 字段
     * @param value 值
     */
    private static void setSpecialField(final Object bean, final Field field, final Object value) {
        if (field == null || value == null) {
            return;
        }
        try {
            ReflectUtil.setFieldValue(bean, field, Convert.convert(field.getGenericType(), value));
        } catch (final Exception exception) {
            log.debug("图字段 {}.{} 赋值失败：{}", field.getDeclaringClass().getName(), field.getName(), value, exception);
        }
    }

    /**
     * 按目标类型转换值
     *
     * @param value       原始值
     * @param targetClass 目标类型
     * @param genericType 目标泛型类型，用于集合元素转换
     * @param <T>         目标类型泛型
     * @return 转换结果，无法转换返回 null
     */
    @SuppressWarnings("unchecked")
    private <T> T convertValue(final Object value, final Class<T> targetClass, final Type genericType) {
        if (value == null) {
            return null;
        }
        if (value instanceof GraphNodeData || value instanceof GraphRelationshipData) {
            return (T) mapElement(value, targetClass);
        }
        if (value instanceof Collection<?> collection && Collection.class.isAssignableFrom(targetClass)) {
            return (T) convertCollection(collection, genericType);
        }
        try {
            return Convert.convert(genericType, value);
        } catch (final Exception exception) {
            log.debug("图属性值 {} 无法转换为 {}", value, genericType, exception);
            return null;
        }
    }

    /**
     * 转换集合值
     * <p>
     * 元素为节点/关系中间表示时逐个递归映射，否则整体交由 hutool 按泛型转换。
     * </p>
     *
     * @param collection  原始集合
     * @param genericType 目标字段的泛型类型
     * @return 转换结果，无法转换返回 null
     */
    private Object convertCollection(final Collection<?> collection, final Type genericType) {
        final Class<?> elementClass = resolveElementClass(genericType);
        if (elementClass != null && GraphClassCache.getMeta(elementClass).isGraphEntity()) {
            final List<Object> resultList = new ArrayList<>(collection.size());
            for (final Object element : collection) {
                if (element instanceof GraphNodeData || element instanceof GraphRelationshipData) {
                    resultList.add(mapElement(element, elementClass));
                }
            }
            return resultList;
        }
        try {
            return Convert.convert(genericType, collection);
        } catch (final Exception exception) {
            log.debug("图集合值无法转换为 {}", genericType, exception);
            return null;
        }
    }

    /**
     * 解析集合字段的元素类型
     *
     * @param genericType 字段泛型类型
     * @return 元素类型，无法解析返回 null
     */
    private static Class<?> resolveElementClass(final Type genericType) {
        if (genericType instanceof ParameterizedType parameterizedType) {
            final Type[] arguments = parameterizedType.getActualTypeArguments();
            if (arguments.length == 1 && arguments[0] instanceof Class<?> elementClass) {
                return elementClass;
            }
        }
        return null;
    }

    /**
     * 确定实际实例化类型
     * <p>
     * 声明类型为基类而按标签能反查到更具体的子类时，实例化子类，
     * 让 {@code RETURN n} 查基类也能得到子类实例。
     * </p>
     *
     * @param declaredClass   声明类型
     * @param registeredClass 按标签反查到的类型
     * @param <T>             声明类型泛型
     * @return 实际实例化类型
     */
    @SuppressWarnings("unchecked")
    private static <T> Class<T> resolveActualClass(final Class<T> declaredClass, final Class<?> registeredClass) {
        if (registeredClass == null || registeredClass.equals(declaredClass)) {
            return declaredClass;
        }
        if (declaredClass.isAssignableFrom(registeredClass)) {
            return (Class<T>) registeredClass;
        }
        return declaredClass;
    }

    /**
     * 获取第一个标签
     *
     * @param nodeData 节点中间表示
     * @return 标签，无标签返回 null
     */
    private static String firstLabel(final GraphNodeData nodeData) {
        return nodeData.getLabelsOrEmpty().stream().findFirst().orElse(null);
    }

    /**
     * 创建目标类型实例
     *
     * @param clz 目标类型
     * @param <T> 目标类型泛型
     * @return 实例
     * @throws ServiceException 无可用无参构造时抛出
     */
    private static <T> T newInstance(final Class<T> clz) {
        try {
            return ReflectUtil.newInstanceIfPossible(clz);
        } catch (final Exception exception) {
            throw new ServiceException(exception, FunCoreDataExceptionEnum.DATA_RESULT_DESERIALIZE_FAILED,
                clz.getName(), exception.getLocalizedMessage());
        }
    }
}
