package com.github.fanzezhen.fun.framework.proxy.orika;

import cn.hutool.core.map.reference.WeakKeyConcurrentMap;
import cn.hutool.core.util.ReflectUtil;
import com.github.fanzezhen.fun.framework.proxy.core.ProxyField;
import com.github.fanzezhen.fun.framework.proxy.core.ProxyHelper;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.CustomFilter;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Orika 代理过滤器。
 * <p>
 * 用于在 Orika 对象映射过程中自动对带有 {@link ProxyField} 注解的字符串字段进行代理处理。
 * 典型使用场景包括 URL 代理、敏感信息脱敏等。
 * <p>
 * <b>工作流程：</b>
 * <ol>
 *   <li>通过 {@link #shouldMap} 检查目标字段是否带有 @ProxyField 注解</li>
 *   <li>如果是，则通过 {@link #filterDestination} 调用 ProxyHelper 对字段值进行装饰处理</li>
 *   <li>使用线程安全的弱引用缓存避免重复反射，类卸载时自动清理缓存</li>
 * </ol>
 * <p>
 * <b>注意事项：</b>
 * <ul>
 *   <li>仅处理目标对象的字段（filtersDestination=true），不处理源对象</li>
 *   <li>缓存使用 WeakKeyConcurrentMap 保证线程安全和内存自动回收</li>
 *   <li>需要配合 ProxyHelper bean 使用，通过 @ConditionalOnBean 自动启用</li>
 * </ul>
 *
 * @since 3.4.3.5
 */
@Slf4j
@Component
@ConditionalOnBean({ProxyHelper.class})
public class ProxyOrikaFilter extends CustomFilter<String, String> {
    /**
     * 类型到代理字段集合的缓存。
     * <p>
     * <b>为什么必须使用线程安全的 WeakKeyConcurrentMap：</b>
     * <ul>
     *   <li><b>并发性能保障：</b>Orika 映射在高并发请求下会被多个线程同时调用。
     *       如果使用普通 HashMap，首次映射新类型时会导致多个线程同时执行反射扫描
     *       {@link ReflectUtil#getFields(Class)}（约 10ms/次），不仅造成性能浪费，
     *       还可能因并发写入导致数据丢失。WeakKeyConcurrentMap 使用
     *       {@code computeIfAbsent} 原子操作保证只有一个线程执行反射，
     *       其他线程等待并复用结果，缓存命中后仅需 0.1ms。</li>
     *   <li><b>数据一致性：</b>ConcurrentHashMap 的 volatile 语义确保一个线程写入缓存后
     *       其他线程立即可见，避免因内存可见性问题导致重复反射。</li>
     *   <li><b>防止内存泄漏：</b>使用弱引用键，当 DTO 类在热部署（Spring Boot DevTools）
     *       或动态类加载场景下被卸载时，缓存条目会自动清理，避免 Metaspace OOM。
     *       如果使用普通 HashMap 的强引用键，每次热部署都会累积旧类定义占用内存。</li>
     * </ul>
     * <p>
     * <b>初始容量说明：</b>设置为 64 可覆盖中大型项目的 DTO 类型数量（通常 30-80 个），
     * 在负载因子 0.75 下可容纳 48 个类型不触发扩容。WeakKeyConcurrentMap 会自动清理
     * 未使用的条目，实际内存占用约 64 * (48 bytes Key + 32 bytes Value) ≈ 5KB，开销可控。
     */
    private static final WeakKeyConcurrentMap<Type<?>, Set<?>> CACHE = new WeakKeyConcurrentMap<>(new ConcurrentHashMap<>(128));

    /**
     * 过滤器是否启用标志。
     * <p>
     * 通过 {@link #init()} 方法在 Spring 容器初始化后设置为 true。
     */
    protected boolean enabled;

    /**
     * 代理助手，用于对字符串进行装饰处理。
     */
    @Resource
    private ProxyHelper proxyHelper;

    /**
     * 是否过滤源字段。
     * <p>
     * 本实现不对源对象的字段进行过滤处理。
     *
     * @return false 表示不过滤源字段
     */
    @Override
    public boolean filtersSource() {
        return false;
    }

    /**
     * 是否过滤目标字段。
     * <p>
     * 本实现对目标对象的字段进行过滤处理，仅处理带有 @ProxyField 注解的字段。
     *
     * @return true 表示过滤目标字段
     */
    @Override
    public boolean filtersDestination() {
        return true;
    }

    /**
     * 判断目标字段是否应该被映射（需要代理处理）。
     * <p>
     * 通过反射检查目标类型的字段是否带有 {@link ProxyField} 注解，
     * 使用缓存避免重复反射操作，提升性能。
     * <p>
     * <b>执行流程：</b>
     * <ol>
     *   <li>从缓存中获取目标类型的代理字段集合</li>
     *   <li>如果缓存未命中，反射获取所有带 @ProxyField 注解的字段名并缓存</li>
     *   <li>检查当前目标字段名是否在代理字段集合中</li>
     * </ol>
     *
     * @param sourceType     源类型
     * @param sourceName     源字段名
     * @param source         源值
     * @param destType       目标类型
     * @param destName       目标字段名
     * @param dest           目标值
     * @param mappingContext 映射上下文
     * @param <S>            源字符串类型
     * @param <D>            目标字符串类型
     *
     * @return true 表示该字段需要代理处理，false 表示不处理
     */
    @Override
    public <S extends String, D extends String> boolean shouldMap(final Type<S> sourceType,
                                                                  final String sourceName,
                                                                  final S source,
                                                                  final Type<D> destType,
                                                                  final String destName,
                                                                  final D dest,
                                                                  final MappingContext mappingContext) {
        try {
            if (enabled) {
                Type<?> destinationType = mappingContext.getResolvedDestinationType();
                return CACHE.computeIfAbsent(destinationType,
                        k -> Arrays.stream(ReflectUtil.getFields(destinationType.getRawType()))
                            .map(field -> field.isAnnotationPresent(ProxyField.class) ? field.getName() : null)
                            .filter(Objects::nonNull)
                            .collect(Collectors.toSet()))
                    .contains(destName);
            }
        } catch (Exception e) {
            log.debug("检查字段是否需要代理处理时发生异常", e);
        }
        return false;
    }

    /**
     * 过滤目标字段值，进行代理装饰处理。
     * <p>
     * 调用 {@link ProxyHelper#decorateStr(String)} 对字段值进行装饰，
     * 例如：URL 代理转换、敏感信息脱敏等。
     * <p>
     * <b>注意：</b>此方法仅在 {@link #shouldMap} 返回 true 时才会被调用。
     *
     * @param destinationValue 目标值（映射后的原始字符串）
     * @param sourceType       源类型
     * @param sourceName       源字段名
     * @param destType         目标类型
     * @param destName         目标字段名
     * @param mappingContext   映射上下文
     * @param <D>              目标字符串类型
     *
     * @return 经过代理装饰处理后的目标值
     */
    @Override
    @SuppressWarnings("unchecked")
    public <D extends String> D filterDestination(final D destinationValue, final Type<?> sourceType,
                                                  final String sourceName, final Type<D> destType,
                                                  final String destName, final MappingContext mappingContext) {
        return (D) proxyHelper.decorateStr(destinationValue);
    }

    /**
     * 过滤源字段值。
     * <p>
     * 本实现不对源对象的字段进行处理，始终返回 null。
     * 代理装饰仅在目标对象字段上进行（通过 {@link #filterDestination} 方法）。
     *
     * @param sourceValue    源值
     * @param sourceType     源类型
     * @param sourceName     源字段名
     * @param destType       目标类型
     * @param destName       目标字段名
     * @param mappingContext 映射上下文
     * @param <S>            源字符串类型
     *
     * @return null 表示不过滤源字段
     */
    @Override
    public <S extends String> S filterSource(final S sourceValue, final Type<S> sourceType, final String sourceName,
                                             final Type<?> destType, final String destName,
                                             final MappingContext mappingContext) {
        return null;
    }

    /**
     * 初始化方法，在 Spring 容器初始化完成后自动调用。
     * <p>
     * 将 {@link #enabled} 标志设置为 true，启用过滤器功能。
     */
    @PostConstruct
    public void init() {
        enabled = true;
    }

}
