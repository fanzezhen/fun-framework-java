package com.github.fanzezhen.fun.framework.data.graph.base.mapping;

import cn.hutool.core.text.CharSequenceUtil;
import org.springframework.util.ConcurrentReferenceHashMap;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 图实体元数据缓存
 * <p>
 * 反射解析结果按类缓存，避免每次映射重复扫描字段与注解。实体类缓存使用弱引用，
 * 类被卸载时条目自动回收，不阻碍类加载器释放。
 * </p>
 *
 * @since 4.1.1
 */
public final class GraphClassCache {

    /**
     * 实体类到元数据的缓存
     */
    private static final Map<Class<?>, GraphEntityMeta> ENTITY_META_CACHE = new ConcurrentReferenceHashMap<>();

    /**
     * 标签到节点实体类的缓存
     * <p>
     * 供按图库返回的标签反查具体实体类使用，解决查询声明为基类、
     * 实际希望映射为子类的场景。
     * </p>
     */
    private static final Map<String, Class<?>> LABEL_CLASS_CACHE = new ConcurrentHashMap<>();

    /**
     * 关系类型到关系实体类的缓存
     */
    private static final Map<String, Class<?>> TYPE_CLASS_CACHE = new ConcurrentHashMap<>();

    private GraphClassCache() {
    }

    /**
     * 获取实体类的映射元数据
     *
     * @param entityClass 实体类型
     * @return 映射元数据
     */
    public static GraphEntityMeta getMeta(final Class<?> entityClass) {
        return ENTITY_META_CACHE.computeIfAbsent(entityClass, GraphEntityMeta::new);
    }

    /**
     * 登记图实体类
     * <p>
     * 建立标签（或关系类型）到实体类的反查关系，通常在启动时扫描实体包后调用。
     * </p>
     *
     * @param entityClass 实体类型
     */
    public static void register(final Class<?> entityClass) {
        final GraphEntityMeta meta = getMeta(entityClass);
        if (!meta.isGraphEntity() || CharSequenceUtil.isEmpty(meta.getLabel())) {
            return;
        }
        if (meta.isNodeEntity()) {
            LABEL_CLASS_CACHE.putIfAbsent(meta.getLabel(), entityClass);
            return;
        }
        TYPE_CLASS_CACHE.putIfAbsent(meta.getLabel(), entityClass);
    }

    /**
     * 按标签反查节点实体类
     *
     * @param label 节点标签
     * @return 实体类，未登记返回 null
     */
    public static Class<?> findNodeClass(final String label) {
        return CharSequenceUtil.isEmpty(label) ? null : LABEL_CLASS_CACHE.get(label);
    }

    /**
     * 按关系类型反查关系实体类
     *
     * @param type 关系类型
     * @return 实体类，未登记返回 null
     */
    public static Class<?> findRelationshipClass(final String type) {
        return CharSequenceUtil.isEmpty(type) ? null : TYPE_CLASS_CACHE.get(type);
    }

    /**
     * 清空缓存
     * <p>
     * 供测试隔离使用，生产代码不应调用。
     * </p>
     */
    public static void clear() {
        ENTITY_META_CACHE.clear();
        LABEL_CLASS_CACHE.clear();
        TYPE_CLASS_CACHE.clear();
    }
}
