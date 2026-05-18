package com.github.fanzezhen.fun.framework.core.springboot.web.mvc.validate;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

/**
 * 值域或枚举校验注解（组合注解）.
 * <p>
 * 支持两种校验方式，可以单独使用或组合使用：
 * <ul>
 *     <li>字符串值域校验：通过values指定允许的字符串集合</li>
 *     <li>枚举值校验：通过enumClass指定枚举类型</li>
 * </ul>
 * <p>
 * 当两种方式都配置时，只要满足其中一种即可通过校验。
 */
@Target({ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValueIn.EnumOrValuesValidator.class)
public @interface ValueIn {
    /**
     * 校验失败时的错误消息.
     *
     * @return 错误消息
     */
    String message() default "参数不合法";

    /**
     * 分组校验.
     *
     * @return 校验组
     */
    Class<?>[] groups() default {};

    /**
     * 载荷.
     *
     * @return 载荷数组
     */
    Class<? extends Payload>[] payload() default {};

    /**
     * 字符串值域（允许的字符串值集合）.
     *
     * @return 值数组
     */
    String[] values() default {};

    /**
     * 枚举类.
     *
     * @return 枚举类型，默认为NoneEnum表示不使用枚举校验
     */
    Class<? extends Enum<?>> enumClass() default NoneEnum.class;

    /**
     * 自定义枚举校验方法名（必须是枚举类中的静态方法，返回boolean）.
     *
     * @return 方法名
     */
    String checkMethod() default "";

    /**
     * 是否忽略大小写（仅对字符串值域有效）.
     *
     * @return true表示忽略大小写
     */
    boolean ignoreCase() default false;

    /**
     * 是否强制需要参数（null值是否允许）.
     *
     * @return true表示不允许null
     */
    boolean required() default false;

    /**
     * 占位枚举类，用于表示未设置enumClass的情况.
     */
    enum NoneEnum { }
    
    /**
     * 值域或枚举约束校验器.
     */
    @Slf4j
    class EnumOrValuesValidator implements ConstraintValidator<ValueIn, Object> {
        /**
         * 是否必填.
         */
        private boolean required;

        /**
         * 错误消息.
         */
        private String message;

        /**
         * 允许的字符串值集合.
         */
        private String[] values;

        /**
         * 是否忽略大小写.
         */
        private boolean ignoreCase;

        /**
         * 枚举类.
         */
        private Class<? extends Enum<?>> enumClass;

        /**
         * 自定义校验方法名.
         */
        private String checkMethod;

        /**
         * 初始化校验器.
         *
         * @param constraintAnnotation 约束注解
         */
        @Override
        public void initialize(final ValueIn constraintAnnotation) {
            ConstraintValidator.super.initialize(constraintAnnotation);
            this.required = constraintAnnotation.required();
            this.message = constraintAnnotation.message();
            this.values = constraintAnnotation.values();
            this.ignoreCase = constraintAnnotation.ignoreCase();
            this.enumClass = constraintAnnotation.enumClass();
            this.checkMethod = constraintAnnotation.checkMethod();
        }

        /**
         * 校验参数值是否合法.
         *
         * @param requestParam 待校验参数
         * @param constraintValidatorContext 约束校验上下文
         * @return true表示校验通过
         */
        @Override
        public boolean isValid(final Object requestParam, final ConstraintValidatorContext constraintValidatorContext) {
            if (requestParam == null) {
                if (required) {
                    constraintValidatorContext.disableDefaultConstraintViolation();
                    constraintValidatorContext.buildConstraintViolationWithTemplate(message)
                            .addConstraintViolation();
                    return false;
                }
                return true;
            }

            boolean result = true;
            // 判断使用哪种校验方式
            if (enumClass != null && !NoneEnum.class.equals(enumClass)) {
                // 枚举校验
                result = validateEnum(requestParam);
            }
            if (values != null && values.length > 0) {
                // 字符串值域校验
                result = validateValues(requestParam);
            }

            if (!result) {
                constraintValidatorContext.disableDefaultConstraintViolation();
                constraintValidatorContext.buildConstraintViolationWithTemplate(message)
                        .addConstraintViolation();
                return false;
            }

            return true;
        }

        /**
         * 校验字符串值域.
         *
         * @param requestParam 待校验参数
         * @return true表示值在允许范围内
         */
        private boolean validateValues(final Object requestParam) {
            String str = String.valueOf(requestParam);
            for (String item : values) {
                if (ignoreCase ? item.equalsIgnoreCase(str) : item.equals(str)) {
                    return true;
                }
            }
            return false;
        }

        /**
         * 校验枚举值.
         *
         * @param requestParam 待校验参数
         * @return true表示是有效的枚举值
         */
        private boolean validateEnum(final Object requestParam) {
            if (designatedMethod()) {
                return checkByEnumMethod(requestParam);
            } else {
                return checkByEnumName(requestParam);
            }
        }

        /**
         * 通过枚举名称校验.
         *
         * @param requestParam 待校验参数
         * @return true表示存在匹配的枚举值
         */
        private boolean checkByEnumName(final Object requestParam) {
            if (!(requestParam instanceof String str)) {
                return false;
            }

            return Arrays.stream(enumClass.getEnumConstants())
                .anyMatch(e -> e.name().equalsIgnoreCase(str));
        }

        /**
         * 通过自定义方法校验.
         *
         * @param requestParam 待校验参数
         * @return true表示校验通过
         */
        private boolean checkByEnumMethod(final Object requestParam) {
            try {
                Method method = enumClass.getMethod(checkMethod, requestParam.getClass());

                if (!Boolean.TYPE.equals(method.getReturnType()) &&
                        !Boolean.class.equals(method.getReturnType())) {
                    return false;
                }

                if (!Modifier.isStatic(method.getModifiers())) {
                    return false;
                }

                Boolean invoke = (Boolean) method.invoke(null, requestParam);
                return invoke != null && invoke;
            } catch (Exception e) {
                log.warn("枚举校验异常: {}", e.getMessage(), e);
                return false;
            }
        }

        /**
         * 判断是否指定了自定义校验方法.
         *
         * @return true表示指定了
         */
        private boolean designatedMethod() {
            return checkMethod != null && !checkMethod.isEmpty();
        }
    }
}
