package com.github.fanzezhen.fun.framework.proxy.orika;

import com.github.fanzezhen.fun.framework.proxy.core.ProxyField;
import com.github.fanzezhen.fun.framework.proxy.core.ProxyHelper;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import ma.glasnost.orika.metadata.ClassMapBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

/**
 * 代理 MapperFactory，自动为带有 @ProxyField 注解的字段注册转换器
 * <p>
 * 在创建类映射时，自动检测源类和目标类的字段，如果字段带有 @ProxyField 注解，
 * 则自动注册 proxyOrikaConverter 转换器
 *
 * @since 3.4.3.5
 */
@Component
@ConditionalOnBean({ProxyHelper.class})
public class ProxyMapperFactory extends DefaultMapperFactory {

    /**
     * 构造方法
     */
    public ProxyMapperFactory() {
        super(new Builder());
    }

    /**
     * 创建类映射，自动为代理字段注册转换器
     *
     * @param sourceClass      源类
     * @param destinationClass 目标类
     * @param <S>              源类型
     * @param <D>              目标类型
     * @return 类映射构建器
     */
    @Override
    public <S, D> ClassMapBuilder<S, D> classMap(final Class<S> sourceClass, final Class<D> destinationClass) {
        ClassMapBuilder<S, D> classMapBuilder = super.classMap(sourceClass, destinationClass);

        Field[] sourceFields = sourceClass.getDeclaredFields();
        Field[] destinationFields = destinationClass.getDeclaredFields();

        for (Field sourceField : sourceFields) {
            for (Field destinationField : destinationFields) {
                if (destinationField.getName().equals(sourceField.getName()) &&
                        (sourceField.isAnnotationPresent(ProxyField.class) ||
                                destinationField.isAnnotationPresent(ProxyField.class))) {
                    classMapBuilder
                            .fieldMap(sourceField.getName(), destinationField.getName())
                            .converter("proxyOrikaConverter")
                            .add()
                            .byDefault()
                            .register();
                }

            }
        }
        return classMapBuilder;
    }

}
