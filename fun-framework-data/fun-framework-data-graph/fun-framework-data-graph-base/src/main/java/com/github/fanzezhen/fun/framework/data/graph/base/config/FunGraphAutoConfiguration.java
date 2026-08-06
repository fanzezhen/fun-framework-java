package com.github.fanzezhen.fun.framework.data.graph.base.config;

import cn.hutool.core.lang.ClassScanner;
import com.github.fanzezhen.fun.framework.data.graph.base.annotation.GraphNode;
import com.github.fanzezhen.fun.framework.data.graph.base.annotation.GraphRelationship;
import com.github.fanzezhen.fun.framework.data.graph.base.mapping.DefaultGraphMapper;
import com.github.fanzezhen.fun.framework.data.graph.base.mapping.GraphClassCache;
import com.github.fanzezhen.fun.framework.data.graph.base.mapping.IGraphMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Set;

/**
 * 图数据库基础自动配置类
 * <p>
 * 提供默认映射引擎，并在启动时扫描图实体登记标签到实体类的反查关系。
 * 预热扫描让首次查询不必承担反射解析成本，也使按标签映射为具体子类得以生效。
 * </p>
 *
 * @since 4.1.1
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(FunGraphProperties.class)
public class FunGraphAutoConfiguration {

    /**
     * 创建默认图结果映射引擎
     * <p>
     * 子项目注册自定义 {@link IGraphMapper} bean 即覆盖本默认实现。
     * </p>
     *
     * @return 映射引擎
     */
    @Bean
    @ConditionalOnMissingBean(IGraphMapper.class)
    public IGraphMapper funGraphMapper() {
        return new DefaultGraphMapper();
    }

    /**
     * 扫描并登记图实体
     * <p>
     * 扫描范围取 {@code fun.data.graph.entity-packages}，未配置则取启动类所在包。
     * </p>
     *
     * @param beanFactory        bean 工厂
     * @param funGraphProperties 图数据库配置
     * @return 图实体登记器
     */
    @Bean
    public GraphEntityRegistrar funGraphEntityRegistrar(final BeanFactory beanFactory,
                                                        final FunGraphProperties funGraphProperties) {
        return new GraphEntityRegistrar(beanFactory, funGraphProperties);
    }

    /**
     * 图实体登记器
     * <p>
     * 独立成 bean 而非在配置类里用 {@code @PostConstruct}，
     * 使登记时机与 bean 生命周期一致，也便于测试单独构造。
     * </p>
     */
    public static class GraphEntityRegistrar {

        /**
         * 已登记的图实体数量
         */
        private final int registeredCount;

        /**
         * 构造时完成扫描与登记
         *
         * @param beanFactory        bean 工厂
         * @param funGraphProperties 图数据库配置
         */
        public GraphEntityRegistrar(final BeanFactory beanFactory, final FunGraphProperties funGraphProperties) {
            this.registeredCount = registerEntities(beanFactory, funGraphProperties);
        }

        /**
         * 获取已登记的图实体数量
         *
         * @return 图实体数量
         */
        public int getRegisteredCount() {
            return registeredCount;
        }

        /**
         * 扫描并登记图实体
         *
         * @param beanFactory        bean 工厂
         * @param funGraphProperties 图数据库配置
         * @return 登记数量
         */
        private static int registerEntities(final BeanFactory beanFactory,
                                            final FunGraphProperties funGraphProperties) {
            int count = 0;
            for (final String packageName : resolveScanPackages(beanFactory, funGraphProperties)) {
                final Set<Class<?>> classSet = ClassScanner.scanPackage(packageName);
                for (final Class<?> clazz : classSet) {
                    if (clazz.isAnnotationPresent(GraphNode.class)
                        || clazz.isAnnotationPresent(GraphRelationship.class)) {
                        GraphClassCache.register(clazz);
                        count++;
                        log.debug("登记图实体：{}", clazz.getName());
                    }
                }
            }
            log.debug("图实体登记完成，共 {} 个", count);
            return count;
        }

        /**
         * 解析实体扫描包
         * <p>
         * 优先取显式配置的扫描包，未配置则退回启动类所在包。
         * </p>
         *
         * @param beanFactory        bean 工厂
         * @param funGraphProperties 图数据库配置
         * @return 包名数组
         */
        private static String[] resolveScanPackages(final BeanFactory beanFactory,
                                                    final FunGraphProperties funGraphProperties) {
            if (funGraphProperties != null && !CollectionUtils.isEmpty(funGraphProperties.getEntityPackages())) {
                return StringUtils.toStringArray(funGraphProperties.getEntityPackages());
            }
            if (AutoConfigurationPackages.has(beanFactory)) {
                return StringUtils.toStringArray(AutoConfigurationPackages.get(beanFactory));
            }
            return new String[0];
        }
    }
}
