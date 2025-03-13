package com.github.fanzezhen.fun.framework.proxy.orika;

import com.github.fanzezhen.fun.framework.proxy.core.ProxyField;
import com.github.fanzezhen.fun.framework.proxy.core.ProxyHelper;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import ma.glasnost.orika.metadata.ClassMapBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

/**
 * @author fanzezhen
 * @since 3.4.3.5
 */
@Component
@ConditionalOnBean({ProxyHelper.class})
public class ProxyMapperFactory extends DefaultMapperFactory {

    public ProxyMapperFactory() {
        super(new Builder());
    }

    @Override
    public <S, D> ClassMapBuilder<S, D> classMap(Class<S> sourceClass, Class<D> destinationClass) {
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
