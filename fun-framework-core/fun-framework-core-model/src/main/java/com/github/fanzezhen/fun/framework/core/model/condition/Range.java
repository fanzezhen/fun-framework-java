package com.github.fanzezhen.fun.framework.core.model.condition;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import com.github.fanzezhen.fun.framework.core.model.IHolder;
import com.github.fanzezhen.fun.framework.core.model.util.ValidUtil;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;

/**
 * 范围条件
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
public class Range<T extends Serializable> implements IHolder, Serializable {
    /**
     * 开始
     */
    private T from;

    /**
     * 结束
     */
    private T to;
    /**
     * 是否包含该起始值
     */
    private Boolean fromEquals;
    /**
     * 是否包含该结束值
     */
    private Boolean toEquals;

    /**
     * 起始值
     */
    public String getFromStr() {
        if (from == null) {
            return null;
        }
        if (from instanceof Date date) {
            return DateUtil.formatDate(date);
        }
        return from.toString();
    }

    /**
     * 终点值
     */
    public String getToStr() {
        if (to == null) {
            return null;
        }
        if (to instanceof Date date) {
            return DateUtil.formatDate(date);
        }
        return to.toString();
    }

    public String getFromAtStartOfDay() {
        return from + " 00:00:00";
    }

    public String getToAtStartOfTomorrow() {
        if (to instanceof LocalDate localDate){
            return LocalDateTimeUtil.formatNormal(localDate.plusDays(1).atStartOfDay());
        }
        return to + " 00:00:00";
    }

    /**
     * 设置起始值
     */
    public static <T extends Serializable> Range<T> from(T from) {
        return new Range<T>().setFrom(from);
    }

    /**
     * 设置终点值
     */
    public static <T extends Serializable> Range<T> to(T to) {
        return new Range<T>().setTo(to);
    }

    /**
     * 判空
     */
    public boolean isNotEmptyFrom() {
        return !ValidUtil.isEmpty(from) ;
    }

    /**
     * 判空
     */
    public boolean isNotEmptyTo() {
        return !ValidUtil.isEmpty(to);
    }

    /**
     * 判空
     */
    public boolean isEmpty() {
        return ValidUtil.isEmpty(from) && ValidUtil.isEmpty(to);
    }
}
