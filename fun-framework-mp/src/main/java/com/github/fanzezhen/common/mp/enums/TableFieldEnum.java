package com.github.fanzezhen.fun.framework.mp.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @author zezhen.fan
 */
public enum TableFieldEnum {
    /**
     * 主键
     */
    PK("id", "id", "主键"),
    /**
     * 已删除
     */
    CREATE_TIME("create_time", "createTime", "创建时间");

    @EnumValue
    @JsonValue
    public final String column;
    public final String field;
    public final String desc;

    TableFieldEnum(String column, String field, String desc) {
        this.column = column;
        this.field = field;
        this.desc = desc;
    }
}
