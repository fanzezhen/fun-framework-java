package com.github.fanzezhen.fun.framework.mp.config;

import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;

/**
 * mybatis-plus父类缓存配置，选择使用SqlSessionFactory实现时不能与ISqlInjector放在同一个bean中
 *
 * @author fanzezhen
 * @since 3.4.3.1
 */
@Configuration
public class FunMpBaseFieldCacheConfig {
    @Resource
    private SqlSessionFactory sqlSessionFactory;

    /**
     * 初始化实体父类缓存配置
     */
    @PostConstruct
    private void init() {
        Class<?>[] entityClasses = new Class[]{
            com.github.fanzezhen.fun.framework.mp.base.entity.increment.BaseEntity.class,
            com.github.fanzezhen.fun.framework.mp.base.entity.increment.tenant.BaseTenantEntity.class,
            com.github.fanzezhen.fun.framework.mp.base.entity.increment.BaseGenericEntity.class,
            com.github.fanzezhen.fun.framework.mp.base.entity.increment.tenant.BaseTenantGenericEntity.class,
            com.github.fanzezhen.fun.framework.mp.base.entity.snowflake.BaseEntity.class,
            com.github.fanzezhen.fun.framework.mp.base.entity.snowflake.tenant.BaseTenantEntity.class,
            com.github.fanzezhen.fun.framework.mp.base.entity.snowflake.BaseGenericEntity.class,
            com.github.fanzezhen.fun.framework.mp.base.entity.snowflake.tenant.BaseTenantGenericEntity.class,
            com.github.fanzezhen.fun.framework.mp.base.entity.uuid.BaseEntity.class,
            com.github.fanzezhen.fun.framework.mp.base.entity.uuid.tenant.BaseTenantEntity.class,
            com.github.fanzezhen.fun.framework.mp.base.entity.uuid.BaseGenericEntity.class,
            com.github.fanzezhen.fun.framework.mp.base.entity.uuid.tenant.BaseTenantGenericEntity.class,
        };
        for (Class<?> entityClass : entityClasses) {
            MapperBuilderAssistant mapperBuilderAssistant = new MapperBuilderAssistant(sqlSessionFactory.getConfiguration(), entityClass.getName());
            mapperBuilderAssistant.setCurrentNamespace(entityClass.getName());
            TableInfoHelper.initTableInfo(mapperBuilderAssistant, entityClass);
        }
    }

}
