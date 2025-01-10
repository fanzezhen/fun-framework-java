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
 * MP配置
 *
 * @author fanzezhen
 * @createTime 2024/5/7 11:51
 */
@Configuration
public class MybatisPlusCacheConfig {
    @Resource
    private SqlSessionFactory sqlSessionFactory;

    /**
     * 初始化实体父类缓存
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
