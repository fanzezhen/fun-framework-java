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
 * 枚举值校验注解.
 * <p>
 * 校验参数值是否为指定枚举类的有效值。
 * 支持两种校验方式：
 * <ul>
 *     <li>枚举名称匹配（默认）</li>
 *     <li>自定义静态校验方法（通过checkMethod指定）</li>
 * </ul>
 */
@Target({ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EnumsOf.EnumCheckValidator.class)
public @interface EnumsOf {
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
     * 枚举类.
     *
     * @return 枚举类型
     */
    Class<? extends Enum<?>> enumClass();

    /**
     * 自定义校验方法名（必须是枚举类中的静态方法，返回boolean）.
     *
     * @return 方法名
     */
    String checkMethod() default "";

    /**
     * 是否强制需要参数（null值是否允许）.
     *
     * @return true表示不允许null
     */
    boolean required() default false;

    /**
     * 枚举值约束校验器.
     */
    @Slf4j
    class EnumCheckValidator implements ConstraintValidator<EnumsOf, Object> {
        /**
         * 枚举类.
         */
        private Class<? extends Enum<?>> enumClass;

        /**
         * 自定义校验方法名.
         */
        private String checkMethod;

        /**
         * 是否必填.
         */
        private boolean required;

        /**
         * 错误消息.
         */
        private String message;

        /**
         * 初始化校验器.
         *
         * @param annotation 约束注解
         */
        @Override
        public void initialize(final EnumsOf annotation) {
            ConstraintValidator.super.initialize(annotation);
            checkMethod = annotation.checkMethod();
            enumClass = annotation.enumClass();
            required = annotation.required();
            message = annotation.message();
        }

        /**
         * 校验参数值是否为有效枚举值.
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

            boolean result;
            if (designatedMethod()) {
                result = checkByEnumMethod(requestParam);
            } else {
                result = checkByEnumName(requestParam);
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
         * 通过枚举名称校验.
         *
         * @param requestParam 待校验参数
         * @return true表示存在匹配的枚举值
         */
        private boolean checkByEnumName(final Object requestParam) {
            if (!(requestParam instanceof String)) {
                return false;
            }

            String str = (String) requestParam;
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
