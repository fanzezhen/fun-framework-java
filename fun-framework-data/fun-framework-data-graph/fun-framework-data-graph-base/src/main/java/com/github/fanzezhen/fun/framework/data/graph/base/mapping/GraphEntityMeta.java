package com.github.fanzezhen.fun.framework.data.graph.base.mapping;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ReflectUtil;
import com.github.fanzezhen.fun.framework.core.model.annotation.Column;
import com.github.fanzezhen.fun.framework.data.graph.base.annotation.EndNode;
import com.github.fanzezhen.fun.framework.data.graph.base.annotation.GraphId;
import com.github.fanzezhen.fun.framework.data.graph.base.annotation.GraphLabels;
import com.github.fanzezhen.fun.framework.data.graph.base.annotation.GraphNode;
import com.github.fanzezhen.fun.framework.data.graph.base.annotation.GraphRelationship;
import com.github.fanzezhen.fun.framework.data.graph.base.annotation.GraphType;
import com.github.fanzezhen.fun.framework.data.graph.base.annotation.StartNode;
import lombok.Getter;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 图实体元数据
 * <p>
 * 一个实体类的全部映射信息聚合在本对象中：标签、各类特殊字段、属性名到字段的映射、
 * 可写属性列表。相比按用途分散成多份缓存，聚合成单一对象让"取元数据"只有一次查找，
 * 也消除了多份缓存彼此不一致的可能。
 * </p>
 *
 * @since 4.1.1
 */
@Getter
public final class GraphEntityMeta {

    /**
     * 实体类型
     */
    private final Class<?> entityClass;

    /**
     * 是否为节点实体
     */
    private final boolean nodeEntity;

    /**
     * 是否为关系实体
     */
    private final boolean relationshipEntity;

    /**
     * 节点标签或关系类型
     * <p>
     * 非图实体（投影 DTO）为 null。
     * </p>
     */
    private final String label;

    /**
     * 图库内部标识字段
     */
    private final Field elementIdField;

    /**
     * 业务主键字段
     */
    private final Field businessIdField;

    /**
     * 业务主键对应的属性名
     */
    private final String businessIdProperty;

    /**
     * 标签集合字段
     */
    private final Field labelsField;

    /**
     * 关系类型字段
     */
    private final Field typeField;

    /**
     * 关系起始节点字段
     */
    private final Field startNodeField;

    /**
     * 关系结束节点字段
     */
    private final Field endNodeField;

    /**
     * 属性名到字段列表的映射
     * <p>
     * 同一属性名可能对应多个字段（如父子类同名字段），故值为列表。
     * </p>
     */
    private final Map<String, List<Field>> propertyFieldMap;

    /**
     * 可写属性名到字段的映射
     * <p>
     * 用于写入语句：排除图库内部标识、标签集合、关系类型、起止节点，
     * 以及 {@code @Column(writable = false)} 标注的只读字段。
     * </p>
     */
    private final Map<String, Field> writablePropertyMap;

    /**
     * 解析实体类的映射元数据
     *
     * @param entityClass 实体类型
     */
    GraphEntityMeta(final Class<?> entityClass) {
        this.entityClass = entityClass;
        final GraphNode graphNode = entityClass.getAnnotation(GraphNode.class);
        final GraphRelationship graphRelationship = entityClass.getAnnotation(GraphRelationship.class);
        this.nodeEntity = graphNode != null;
        this.relationshipEntity = graphRelationship != null;
        this.label = resolveLabel(entityClass, graphNode, graphRelationship);

        final SpecialFields specialFields = new SpecialFields();
        final Map<String, List<Field>> propertyFields = new LinkedHashMap<>();
        final Map<String, Field> writableFields = new LinkedHashMap<>();
        Field resolvedBusinessIdField = null;
        String resolvedBusinessIdProperty = null;

        for (final Field field : ReflectUtil.getFields(entityClass)) {
            if (isIgnorable(field) || specialFields.assign(field)) {
                continue;
            }
            final Column column = field.getAnnotation(Column.class);
            final String propertyName = resolvePropertyName(field, column);
            propertyFields.computeIfAbsent(propertyName, key -> new ArrayList<>()).add(field);
            if (column != null && column.isPrimaryKey()) {
                resolvedBusinessIdField = field;
                resolvedBusinessIdProperty = propertyName;
            }
            if ((column == null || column.writable()) && !writableFields.containsKey(propertyName)) {
                writableFields.put(propertyName, field);
            }
        }

        this.elementIdField = specialFields.elementId;
        this.businessIdField = resolvedBusinessIdField;
        this.businessIdProperty = resolvedBusinessIdProperty;
        this.labelsField = specialFields.labels;
        this.typeField = specialFields.type;
        this.startNodeField = specialFields.startNode;
        this.endNodeField = specialFields.endNode;
        this.propertyFieldMap = Collections.unmodifiableMap(propertyFields);
        this.writablePropertyMap = Collections.unmodifiableMap(writableFields);
    }

    /**
     * 判断是否为图实体（节点或关系）
     *
     * @return true 表示图实体
     */
    public boolean isGraphEntity() {
        return nodeEntity || relationshipEntity;
    }

    /**
     * 获取指定属性名对应的字段列表
     *
     * @param propertyName 属性名
     * @return 字段列表，无匹配返回空列表
     */
    public List<Field> getFields(final String propertyName) {
        return propertyFieldMap.getOrDefault(propertyName, Collections.emptyList());
    }

    /**
     * 解析节点标签或关系类型
     *
     * @param entityClass       实体类型
     * @param graphNode         节点注解
     * @param graphRelationship 关系注解
     * @return 标签或类型，非图实体返回 null
     */
    private static String resolveLabel(final Class<?> entityClass,
                                       final GraphNode graphNode,
                                       final GraphRelationship graphRelationship) {
        if (graphNode != null) {
            return CharSequenceUtil.emptyToDefault(graphNode.label(), entityClass.getSimpleName());
        }
        if (graphRelationship != null) {
            return CharSequenceUtil.emptyToDefault(graphRelationship.type(), entityClass.getSimpleName());
        }
        return null;
    }

    /**
     * 解析字段对应的属性名
     * <p>
     * 优先取 {@link Column#name()}，未指定则用字段名。
     * </p>
     *
     * @param field  字段
     * @param column 列注解，可为 null
     * @return 属性名
     */
    private static String resolvePropertyName(final Field field, final Column column) {
        if (column != null && CharSequenceUtil.isNotEmpty(column.name())) {
            return column.name();
        }
        return field.getName();
    }

    /**
     * 判断字段是否应被忽略
     * <p>
     * 静态字段与编译器合成字段（如内部类的 {@code this$0}）不参与映射。
     * </p>
     *
     * @param field 字段
     * @return true 表示忽略
     */
    private static boolean isIgnorable(final Field field) {
        return Modifier.isStatic(field.getModifiers()) || field.isSynthetic();
    }

    /**
     * 特殊字段收集器
     * <p>
     * 图专属注解标注的字段（标识、标签、关系类型、起止节点）不作为普通属性参与映射，
     * 集中在此识别并暂存，使解析循环只需判断"是否为特殊字段"这一件事。
     * </p>
     */
    private static final class SpecialFields {

        /**
         * 图库内部标识字段
         */
        private Field elementId;

        /**
         * 标签集合字段
         */
        private Field labels;

        /**
         * 关系类型字段
         */
        private Field type;

        /**
         * 关系起始节点字段
         */
        private Field startNode;

        /**
         * 关系结束节点字段
         */
        private Field endNode;

        /**
         * 若字段带图专属注解则收下并返回 true，否则返回 false 交由普通属性逻辑处理
         *
         * @param field 字段
         * @return true 表示已作为特殊字段收下
         */
        private boolean assign(final Field field) {
            if (field.isAnnotationPresent(GraphId.class)) {
                elementId = field;
            } else if (field.isAnnotationPresent(GraphLabels.class)) {
                labels = field;
            } else if (field.isAnnotationPresent(GraphType.class)) {
                type = field;
            } else if (field.isAnnotationPresent(StartNode.class)) {
                startNode = field;
            } else if (field.isAnnotationPresent(EndNode.class)) {
                endNode = field;
            } else {
                return false;
            }
            return true;
        }
    }
}
