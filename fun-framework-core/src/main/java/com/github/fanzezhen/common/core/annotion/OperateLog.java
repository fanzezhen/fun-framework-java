package com.github.fanzezhen.fun.framework.core.annotion;

import cn.hutool.core.util.StrUtil;
import com.github.fanzezhen.fun.framework.core.dict.AbstractDict;

import java.lang.annotation.*;


/**
 * @author zezhen.fan
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperateLog {
    Class<? extends AbstractDict> dictClass();

    String tableName() default StrUtil.EMPTY;

    boolean isAllFields() default true;

    String[] fieldNameFilters() default {};

    String serviceBeanName() default StrUtil.EMPTY;

    String mapperBeanName() default StrUtil.EMPTY;
}
