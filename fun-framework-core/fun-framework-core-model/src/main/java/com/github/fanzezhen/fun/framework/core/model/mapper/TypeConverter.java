package com.github.fanzezhen.fun.framework.core.model.mapper;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * MethodHandle 引擎的类型转换矩阵。
 * <p>
 * 覆盖 Orika 内置的常见基础类型互转，保证从 Orika 迁移到 MethodHandle 引擎后行为不回退：
 * <ul>
 *   <li>同类型 / 可直接赋值：原样返回</li>
 *   <li>数值互转：Integer/Long/Short/Byte/Double/Float/BigDecimal/BigInteger 之间</li>
 *   <li>字符串互转：任意基础类型 ↔ String</li>
 *   <li>枚举互转：String ↔ Enum</li>
 *   <li>布尔互转：String/Number ↔ Boolean</li>
 *   <li>日期互转：Date / LocalDate / LocalDateTime / Instant / 时间戳 long 之间</li>
 * </ul>
 * 无法识别的类型对返回原值（由上层决定是否可赋值），不静默丢弃为 null。
 * </p>
 * <p>
 * 本类内部统一以 {@link Instant} 为归一化中枢，{@link java.util.Date} 仅作为遗留 DTO 字段的
 * 入口/出口类型存在，不参与任何日期运算，因此不适用"改用 java.time"的整改。
 * </p>
 * <p>
 * {@code java.util.Date} 刻意不 import 而用全限定名：import 语句位于类声明之外，
 * 类级 {@link SuppressWarnings} 覆盖不到，只有把引用收进类体内才能被 {@code java:S2143} 的抑制覆盖。
 * </p>
 *
 * @since 4.1.0
 */
@SuppressWarnings("java:S2143") // 须桥接遗留 java.util.Date，Date 互转是本类既定职责，不可移除
final class TypeConverter {

    private TypeConverter() {
        throw new UnsupportedOperationException("工具类不允许实例化");
    }

    /**
     * 将源值转换为目标字段类型。
     *
     * @param value      源值（可能为 null）
     * @param targetType 目标字段类型
     *
     * @return 转换后的值；value 为 null 时返回 null
     */
    static Object convert(final Object value, final Class<?> targetType) {
        if (value == null) {
            return null;
        }
        if (targetType.isInstance(value)) {
            return value;
        }
        Class<?> boxed = boxed(targetType);
        if (boxed.isInstance(value)) {
            return value;
        }
        if (boxed == String.class) {
            return stringValue(value);
        }
        if (Number.class.isAssignableFrom(boxed)) {
            return toNumber(value, boxed);
        }
        if (boxed == Boolean.class) {
            return toBoolean(value);
        }
        if (boxed == Character.class) {
            return toCharacter(value);
        }
        if (boxed.isEnum()) {
            return toEnum(value, boxed);
        }
        if (isDateLike(boxed)) {
            return toDateLike(value, boxed);
        }
        return value;
    }

    /**
     * 基础类型的装箱类型；引用类型原样返回。
     */
    private static Class<?> boxed(final Class<?> type) {
        if (!type.isPrimitive()) {
            return type;
        }
        if (type == int.class) {
            return Integer.class;
        }
        if (type == long.class) {
            return Long.class;
        }
        if (type == double.class) {
            return Double.class;
        }
        if (type == float.class) {
            return Float.class;
        }
        if (type == short.class) {
            return Short.class;
        }
        if (type == byte.class) {
            return Byte.class;
        }
        if (type == boolean.class) {
            return Boolean.class;
        }
        if (type == char.class) {
            return Character.class;
        }
        return type;
    }

    private static String stringValue(final Object value) {
        return String.valueOf(value);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static Object toEnum(final Object value, final Class<?> enumType) {
        if (value instanceof CharSequence) {
            return Enum.valueOf((Class<? extends Enum>) enumType, value.toString());
        }
        if (value instanceof Number number) {
            Object[] constants = enumType.getEnumConstants();
            int ordinal = number.intValue();
            if (ordinal >= 0 && ordinal < constants.length) {
                return constants[ordinal];
            }
        }
        return value;
    }

    private static Object toBoolean(final Object value) {
        if (value instanceof Number number) {
            return number.doubleValue() != 0d;
        }
        return Boolean.parseBoolean(value.toString());
    }

    private static Object toCharacter(final Object value) {
        String s = value.toString();
        return s.isEmpty() ? null : s.charAt(0);
    }

    private static boolean isDateLike(final Class<?> type) {
        return type == java.util.Date.class
                || type == LocalDate.class
                || type == LocalDateTime.class
                || type == Instant.class;
    }

    /**
     * 转换为目标数值类型；源为字符串或其它数值均支持。
     */
    private static Object toNumber(final Object value, final Class<?> targetType) {
        double d;
        long l;
        if (value instanceof Number number) {
            d = number.doubleValue();
            l = number.longValue();
        } else {
            String s = value.toString().trim();
            if (s.isEmpty()) {
                return null;
            }
            d = Double.parseDouble(s);
            l = (long) d;
        }
        if (targetType == Integer.class) {
            return (int) l;
        }
        if (targetType == Long.class) {
            return l;
        }
        if (targetType == Double.class) {
            return d;
        }
        if (targetType == Float.class) {
            return (float) d;
        }
        if (targetType == Short.class) {
            return (short) l;
        }
        if (targetType == Byte.class) {
            return (byte) l;
        }
        if (targetType == BigDecimal.class) {
            return BigDecimal.valueOf(d);
        }
        if (targetType == BigInteger.class) {
            return BigInteger.valueOf(l);
        }
        return value;
    }

    /**
     * 转换为目标日期类型；支持 Date/LocalDate/LocalDateTime/Instant/时间戳互转。
     */
    private static Object toDateLike(final Object value, final Class<?> targetType) {
        Instant instant = toInstant(value);
        if (instant == null) {
            return value;
        }
        if (targetType == java.util.Date.class) {
            return java.util.Date.from(instant);
        }
        if (targetType == Instant.class) {
            return instant;
        }
        ZoneId zone = ZoneId.systemDefault();
        if (targetType == LocalDate.class) {
            return instant.atZone(zone).toLocalDate();
        }
        if (targetType == LocalDateTime.class) {
            return instant.atZone(zone).toLocalDateTime();
        }
        return value;
    }

    /**
     * 将常见时间表示归一化为 Instant；无法识别返回 null。
     */
    private static Instant toInstant(final Object value) {
        ZoneId zone = ZoneId.systemDefault();
        if (value instanceof java.util.Date date) {
            return date.toInstant();
        }
        if (value instanceof Instant i) {
            return i;
        }
        if (value instanceof LocalDateTime ldt) {
            return ldt.atZone(zone).toInstant();
        }
        if (value instanceof LocalDate ld) {
            return ld.atStartOfDay(zone).toInstant();
        }
        if (value instanceof Number number) {
            return Instant.ofEpochMilli(number.longValue());
        }
        return null;
    }
}
