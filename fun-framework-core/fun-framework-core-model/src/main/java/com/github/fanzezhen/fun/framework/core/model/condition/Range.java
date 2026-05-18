package com.github.fanzezhen.fun.framework.core.model.condition;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import com.github.fanzezhen.fun.framework.core.model.common.IHolder;
import com.github.fanzezhen.fun.framework.core.model.util.ValidUtil;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;

/**
 * 范围条件
 * <p>
 * 用于表示范围查询条件，包含起始值、结束值以及是否包含边界值的配置。
 * </p>
 *
 * @param <T> 范围值类型
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
public class Range<T extends Serializable> implements IHolder, Serializable {
    /**
     * 范围起始值
     */
    private T from;

    /**
     * 范围结束值
     */
    private T to;

    /**
     * 是否包含起始值（闭区间）
     */
    private Boolean fromEquals;

    /**
     * 是否包含结束值（闭区间）
     */
    private Boolean toEquals;

    /**
     * 获取起始值的字符串表示
     * <p>
     * 如果是日期类型，格式化为 yyyy-MM-dd 格式；其他类型直接调用 toString()
     * </p>
     *
     * @return 起始值字符串，如果 from 为 null 则返回 null
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
     * 获取结束值的字符串表示
     * <p>
     * 如果是日期类型，格式化为 yyyy-MM-dd 格式；其他类型直接调用 toString()
     * </p>
     *
     * @return 结束值字符串，如果 to 为 null 则返回 null
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

    /**
     * 获取起始值当天的开始时间
     * <p>
     * 返回格式：yyyy-MM-dd 00:00:00
     * </p>
     *
     * @return 起始值当天开始时间字符串
     */
    public String getFromAtStartOfDay() {
        return from + " 00:00:00";
    }

    /**
     * 获取结束值次日的开始时间
     * <p>
     * 如果是 LocalDate 类型，返回次日 00:00:00；
     * 其他类型返回当天 00:00:00
     * </p>
     *
     * @return 结束值次日开始时间字符串
     */
    public String getToAtStartOfTomorrow() {
        if (to instanceof LocalDate localDate){
            return LocalDateTimeUtil.formatNormal(localDate.plusDays(1).atStartOfDay());
        }
        return to + " 00:00:00";
    }

    /**
     * 创建只包含起始值的范围对象
     *
     * @param from 起始值
     * @param <T>  范围值类型
     * @return 范围对象
     */
    public static <T extends Serializable> Range<T> from(T from) {
        return new Range<T>().setFrom(from);
    }

    /**
     * 创建只包含结束值的范围对象
     *
     * @param to 结束值
     * @param <T> 范围值类型
     * @return 范围对象
     */
    public static <T extends Serializable> Range<T> to(T to) {
        return new Range<T>().setTo(to);
    }

    /**
     * 判断起始值是否非空
     *
     * @return true 表示起始值非空，false 表示起始值为空
     */
    public boolean isNotEmptyFrom() {
        return !ValidUtil.isEmpty(from) ;
    }

    /**
     * 判断结束值是否非空
     *
     * @return true 表示结束值非空，false 表示结束值为空
     */
    public boolean isNotEmptyTo() {
        return !ValidUtil.isEmpty(to);
    }

    /**
     * 判断范围是否为空
     * <p>
     * 起始值和结束值都为空时，范围为空
     * </p>
     *
     * @return true 表示范围为空，false 表示范围非空
     */
    public boolean isEmpty() {
        return ValidUtil.isEmpty(from) && ValidUtil.isEmpty(to);
    }
}
