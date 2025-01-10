package com.github.fanzezhen.fun.framework.core.dict;

import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 字典映射抽象字典类
 *
 * @author zezhen.fan
 */
@Data
public abstract class AbstractDict {
    public static final String INSTANCE_METHOD_NAME = "getInstance";
    /**
     * 字典映射，实体的字段名→字段标题
     */
    protected LinkedHashMap<String, String> dict = new LinkedHashMap<>(8);
    /**
     * 字典映射，实体标题→实体的字段名
     */
    protected LinkedHashMap<String, String> reverseDict = new LinkedHashMap<>(8);
    /**
     * 字段枚举值映射，实体的字段名→{枚举名：枚举值}
     */
    protected LinkedHashMap<String, Map<Object, String>> fieldWrapperDict = new LinkedHashMap<>(8);
    /**
     * 字段枚举值映射，实体的字段名→{枚举值：枚举名}
     */
    protected LinkedHashMap<String, Map<String, Object>> fieldAdapterDict = new LinkedHashMap<>(8);

    protected AbstractDict() {
        put("id", "主键id");
        put("name", "名称");

        putReverse("ID", "id");
        putReverse("名称", "name");

        init();
        initReverse();
        initBeWrapped();
        initBeAdapter();
    }

    /**
     * 初始化字段英文名称和中文名称对应的字典
     *
     * @author zezhen.fan
     */
    public abstract void init();

    /**
     * 初始化字段中文名称和英文名称对应的字典
     *
     * @author zezhen.fan
     */
    protected abstract void initReverse();

    /**
     * 初始化需要被包装的字段(例如:性别为1:男,0:女,需要被包装为汉字)
     *
     * @author zezhen.fan
     */
    protected abstract void initBeWrapped();

    /**
     * 初始化需要被解包装的字段(例如:性别为男:1,女:0,需要被解包装为代码)
     *
     * @author zezhen.fan
     */
    protected abstract void initBeAdapter();

    public boolean containsKey(Object key) {
        return dict.containsKey(String.valueOf(key));
    }

    public boolean containsReverseKey(Object key) {
        return reverseDict.containsKey(String.valueOf(key));
    }

    public String get(String key) {
        if (this.dict.get(key) == null) {
            return key;
        }
        return this.dict.get(key);
    }

    public void put(String key, String value) {
        this.dict.put(key, value);
    }

    public String getReverse(String key) {
        if (this.reverseDict.get(key) == null) {
            return key;
        }
        return this.reverseDict.get(key);
    }

    public void putReverse(String key, String value) {
        this.reverseDict.put(key, value);
    }

    public Map<Object, String> getFieldWrapper(String key) {
        return this.fieldWrapperDict.get(key);
    }

    public void putFieldWrapper(String key, Map<Object, String> fieldWrapper) {
        this.fieldWrapperDict.put(key, fieldWrapper);
    }

    public String getFieldWrapper(String field, Object value) {
        try {
            return this.fieldWrapperDict.get(field).get(value);
        } catch (NullPointerException nullPointerException) {
            return String.valueOf(value);
        }
    }

    public void putFieldWrapper(String field, Object value, String desc) {
        if (this.fieldWrapperDict.containsKey(field)) {
            getFieldWrapper(field).put(value, desc);
        } else {
            LinkedHashMap<Object, String> linkedHashMap = new LinkedHashMap<>(2);
            linkedHashMap.put(value, desc);
            putFieldWrapper(field, linkedHashMap);
        }
    }

    public Map<String, Object> getFieldAdapter(String key) {
        return this.fieldAdapterDict.get(key);
    }

    public void putFieldAdapter(String key, Map<String, Object> fieldAdapter) {
        this.fieldAdapterDict.put(key, fieldAdapter);
    }

    public Object getFieldAdapter(String field, String value) {
        try {
            return this.fieldAdapterDict.get(field).get(value);
        } catch (NullPointerException nullPointerException) {
            return value;
        }
    }

    public void putFieldAdapter(String field, String value, Object desc) {
        this.fieldAdapterDict.get(field).put(value, desc);
    }

    public Object getFieldAdapterByReverse(String reverseKey, String value) {
        return getFieldAdapter(getReverse(reverseKey), value);
    }

}
