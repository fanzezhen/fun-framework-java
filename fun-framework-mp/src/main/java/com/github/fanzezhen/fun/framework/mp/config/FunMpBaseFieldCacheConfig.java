package com.github.fanzezhen.fun.framework.mp.config;

import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.github.fanzezhen.fun.framework.mp.base.entity.BaseEntity;
import com.github.fanzezhen.fun.framework.mp.base.entity.BaseGenericEntity;
import com.github.fanzezhen.fun.framework.mp.base.entity.tenant.BaseTenantEntity;
import com.github.fanzezhen.fun.framework.mp.base.entity.tenant.BaseTenantGenericEntity;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

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
        Class<?>[] entityClasses = new Class[]{BaseEntity.class, BaseTenantEntity.class, BaseGenericEntity.class, BaseTenantGenericEntity.class};
        for (Class<?> entityClass : entityClasses) {
            MapperBuilderAssistant mapperBuilderAssistant = new MapperBuilderAssistant(sqlSessionFactory.getConfiguration(), entityClass.getName());
            mapperBuilderAssistant.setCurrentNamespace(entityClass.getName());
            TableInfoHelper.initTableInfo(mapperBuilderAssistant, entityClass);
        }
    }

}
