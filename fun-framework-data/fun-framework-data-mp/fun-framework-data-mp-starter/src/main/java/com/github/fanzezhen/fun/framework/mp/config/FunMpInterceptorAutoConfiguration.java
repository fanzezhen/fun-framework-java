package com.github.fanzezhen.fun.framework.mp.config;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

import java.util.List;
import java.util.Objects;

/**
 * MyBatis-Plus 拦截器自动配置类
 * <p>
 * 负责自动注册用户定义的 {@link InnerInterceptor} 到 {@link MybatisPlusInterceptor} 中。
 * 启动时会检查是否配置了 {@link PaginationInnerInterceptor}，如果未配置会输出警告信息。
 * <p>
 * 注意事项：
 * <ul>
 *   <li>必须提供 {@link PaginationInnerInterceptor} Bean，否则分页功能不可用</li>
 *   <li>自动去重，相同的拦截器实例不会重复添加</li>
 * </ul>
 *
 * @since 3.4.3.1
 */
@Slf4j
@Configuration
public class FunMpInterceptorAutoConfiguration {
    /**
     * MyBatis-Plus 拦截器，容器中不存在时为 {@code null}
     * <p>
     * 用 {@link ObjectProvider} 而非 {@code @ConditionalOnBean(MybatisPlusInterceptor.class)}：
     * 本类由组件扫描注册，条件注解在扫描期求值，那时 {@link FunMpAutoConfiguration} 的
     * {@code @Bean} 定义可能尚未注册，导致本类被静默跳过、拦截器收编不发生。
     */
    public final MybatisPlusInterceptor mybatisPlusInterceptor;

    /**
     * 用户定义的内部拦截器列表
     */
    public final List<InnerInterceptor> innerInterceptors;

    /**
     * 构造函数
     *
     * @param mybatisPlusInterceptorProvider MyBatis-Plus 拦截器提供者
     * @param innerInterceptors              用户定义的内部拦截器列表（可选）
     */
    public FunMpInterceptorAutoConfiguration(
            final ObjectProvider<MybatisPlusInterceptor> mybatisPlusInterceptorProvider,
            @Autowired(required = false) final List<InnerInterceptor> innerInterceptors) {
        this.mybatisPlusInterceptor = mybatisPlusInterceptorProvider.getIfAvailable();
        this.innerInterceptors = innerInterceptors;
    }

    /**
     * 初始化拦截器
     * <p>
     * 自动注册用户定义的内部拦截器，并检查是否配置了分页拦截器。
     */
    @PostConstruct
    private void init() {
        if (mybatisPlusInterceptor != null && CollUtil.isNotEmpty(innerInterceptors)) {
            for (InnerInterceptor innerInterceptor : innerInterceptors) {
                if (mybatisPlusInterceptor.getInterceptors().stream().noneMatch(
                    i -> Objects.equals(i, innerInterceptor))) {
                    mybatisPlusInterceptor.addInnerInterceptor(innerInterceptor);
                }
            }
        }
        if (mybatisPlusInterceptor == null || mybatisPlusInterceptor.getInterceptors().stream().noneMatch(PaginationInnerInterceptor.class::isInstance)) {
            log.warn("""
                
                ================================================================================
                ⚠️  警告: 未检测到 PaginationInnerInterceptor 配置!
                ================================================================================
                
                【问题】分页功能将不可用,分页查询将返回空数据或全量数据
                
                【解决方案】请在项目中添加以下配置:
                
                    @Configuration
                    public class MybatisPlusConfig {
                        @Bean
                        public InnerInterceptor paginationInnerInterceptor() {
                            return new PaginationInnerInterceptor(DbType.MYSQL);
                        }
                    }
                
                【详细文档】
                - README: https://github.com/fanzezhen/fun-framework-java/blob/master/fun-framework-data/fun-framework-data-mp/fun-framework-data-mp-starter/README.md#2-%EF%B8%8F-%E5%BF%85%E9%9C%80%E9%85%8D%E7%BD%AE%E9%87%8D%E8%A6%81
                - 示例代码: https://github.com/fanzezhen/fun-framework-java/blob/master/fun-framework-data/fun-framework-data-mp/fun-framework-data-mp-starter/src/test/java/com/github/fanzezhen/fun/framework/mp/example/MybatisPlusExampleConfig.java
                
                ================================================================================
                """);
        }
    }

}
