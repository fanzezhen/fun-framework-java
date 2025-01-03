package com.github.fanzezhen.fun.framework.core.struct;


import com.github.fanzezhen.fun.framework.core.dict.AbstractDict;

/**
 * @author zezhen.fan
 */
public abstract class AbstractBizLogStruct {
    protected static String serviceBeanName;
    protected static String beanMethodName = "getById";
    protected static AbstractDict dict;

    public String getServiceBeanName() {
        return serviceBeanName;
    }

    public String getBeanMethodName() {
        return beanMethodName;
    }

    public AbstractDict getDict() {
        return dict;
    }
}
