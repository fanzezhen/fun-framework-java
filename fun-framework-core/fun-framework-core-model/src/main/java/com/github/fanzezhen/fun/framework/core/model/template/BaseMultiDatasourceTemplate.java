package com.github.fanzezhen.fun.framework.core.model.template;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.text.CharSequenceUtil;
import com.github.fanzezhen.fun.framework.core.model.annotation.Entity;
import com.github.fanzezhen.fun.framework.core.model.enums.FunCoreDataExceptionEnum;
import com.github.fanzezhen.fun.framework.core.model.exception.ServiceException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 多数据源模板抽象基类
 * <p>
 * 承载与具体存储无关的多数据源能力：按名称索引配置、按实体上的
 * {@link Entity#datasource()} 路由、默认数据源回退、子模板创建与缓存。
 * Elasticsearch、图数据库等多数据源模板可共用本类，无需各自重复实现路由逻辑。
 * </p>
 * <p>
 * 子模板默认惰性创建：构造期只校验配置。之所以不在构造期创建，是因为子类的
 * {@link #createTemplate} 通常要用到子类字段，而 Java 的初始化顺序是父类构造器
 * 先于子类字段赋值，构造期回调子类方法必然读到未初始化的字段。需要启动即创建的
 * 实现，可在自身构造器末尾调用 {@link #initAllTemplates()}。
 * </p>
 *
 * @param <T> 子模板类型
 * @param <C> 数据源配置类型
 * @since 4.1.1
 */
public abstract class BaseMultiDatasourceTemplate<T, C extends IDatasourceConfig> {

    /**
     * 默认数据源名称
     */
    protected final String defaultDatasourceName;

    /**
     * 存储类型标识，用于异常信息区分数据源类型
     */
    protected final String datasourceMark;

    /**
     * 数据源名称到配置的映射
     */
    protected final Map<String, C> configMap;

    /**
     * 数据源名称到子模板的映射
     */
    protected final Map<String, T> templateMap;

    /**
     * 构造方法
     *
     * @param defaultDatasourceName 默认数据源名称
     * @param configs               数据源配置列表，可为 null
     * @param datasourceMark        存储类型标识，用于异常信息
     * @throws ServiceException 数据源名称重复时抛出
     */
    protected BaseMultiDatasourceTemplate(final String defaultDatasourceName,
                                          final List<C> configs,
                                          final String datasourceMark) {
        this.defaultDatasourceName = defaultDatasourceName;
        this.datasourceMark = datasourceMark;
        this.templateMap = new ConcurrentHashMap<>();
        this.configMap = new LinkedHashMap<>();
        if (configs == null) {
            return;
        }
        for (final C config : configs) {
            if (configMap.containsKey(config.getName())) {
                throw new ServiceException(FunCoreDataExceptionEnum.TEMPLATE_IMPL_NAME_DUPLICATED,
                    config.getName() + datasourceMark);
            }
            configMap.put(config.getName(), config);
        }
    }

    /**
     * 按配置创建子模板
     *
     * @param config 数据源配置
     * @return 子模板实例
     */
    protected abstract T createTemplate(C config);

    /**
     * 按数据源名称取子模板
     *
     * @param datasource 数据源名称，为空时取默认数据源
     * @return 子模板实例
     * @throws ServiceException 指定数据源不存在时抛出
     */
    public T findTemplate(final String datasource) {
        final String targetName = CharSequenceUtil.isEmpty(datasource) ? defaultDatasourceName : datasource;
        if (!configMap.containsKey(targetName)) {
            throw new ServiceException(FunCoreDataExceptionEnum.TEMPLATE_IMPL_NOT_EXISTS,
                targetName + datasourceMark);
        }
        return obtainTemplate(targetName);
    }

    /**
     * 按实体类型路由子模板
     * <p>
     * 取实体上 {@link Entity#datasource()} 声明的数据源，未声明则走默认数据源。
     * </p>
     *
     * @param clz 实体类型
     * @return 子模板实例
     */
    public T findTemplate(final Class<?> clz) {
        final Entity entity = clz == null ? null : AnnotationUtil.getAnnotation(clz, Entity.class);
        return findTemplate(entity == null ? null : entity.datasource());
    }

    /**
     * 取默认数据源子模板，默认数据源未配置时退化为任一可用数据源
     *
     * @return 子模板实例
     * @throws ServiceException 未配置任何数据源时抛出
     */
    public T getDefaultOrAnyTemplate() {
        if (defaultDatasourceName != null && configMap.containsKey(defaultDatasourceName)) {
            return obtainTemplate(defaultDatasourceName);
        }
        if (!configMap.isEmpty()) {
            return obtainTemplate(configMap.keySet().iterator().next());
        }
        throw new ServiceException(FunCoreDataExceptionEnum.TEMPLATE_IMPL_CONFIG_NOT_EXISTS, datasourceMark);
    }

    /**
     * 预先创建全部子模板
     * <p>
     * 供需要启动即创建（而非首次使用才创建）的实现在自身构造器末尾调用，
     * 此时子类字段已完成初始化。
     * </p>
     */
    protected void initAllTemplates() {
        configMap.keySet().forEach(this::obtainTemplate);
    }

    /**
     * 取（必要时创建）指定数据源的子模板
     *
     * @param datasourceName 数据源名称
     * @return 子模板实例
     * @throws ServiceException 子模板创建返回 null 时抛出
     */
    protected T obtainTemplate(final String datasourceName) {
        final T template = templateMap.computeIfAbsent(datasourceName, name -> createTemplate(configMap.get(name)));
        if (Objects.isNull(template)) {
            throw new ServiceException(FunCoreDataExceptionEnum.TEMPLATE_IMPL_CREATE_NULL,
                datasourceName + datasourceMark);
        }
        return template;
    }

    /**
     * 取全部已配置的数据源名称
     *
     * @return 数据源名称列表
     */
    public List<String> listDatasourceNames() {
        return new ArrayList<>(configMap.keySet());
    }

    /**
     * 取已创建的子模板
     * <p>
     * 惰性创建下未被使用的数据源不会出现在结果中，供资源释放时遍历。
     * </p>
     *
     * @return 已创建的子模板集合
     */
    protected Collection<T> listCreatedTemplates() {
        return templateMap.values();
    }
}
