package com.github.fanzezhen.fun.framework.core.springboot.web.mvc.validate;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 值域范围校验注解.
 * <p>
 * 校验参数值是否在指定的字符串集合中，支持大小写忽略。
 * 基于 Spring-Hibernate Validate 实现。
 * <p>
 * 示例：
 * <pre>{@code
 * @BelongTo(required = true, values = {"SSO", "NATIVE"},
 *          message = "登录模式目前仅支持SSO, NATIVE两种")
 * private String loginMode;
 * }</pre>
 */
@Target({ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = BelongTo.BelongToConstraintValidator.class)
public @interface BelongTo {
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
     * 允许的值集合.
     *
     * @return 值数组
     */
    String[] values();

    /**
     * 是否忽略大小写.
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
     * 值域范围约束校验器.
     */
    class BelongToConstraintValidator implements ConstraintValidator<BelongTo, Object> {
        /**
         * 是否必填.
         */
        private boolean required;

        /**
         * 错误消息.
         */
        private String message;

        /**
         * 允许的值集合.
         */
        private String[] values;

        /**
         * 是否忽略大小写.
         */
        private boolean ignoreCase;

        /**
         * 初始化校验器.
         *
         * @param constraintAnnotation 约束注解
         */
        @Override
        public void initialize(final BelongTo constraintAnnotation) {
            ConstraintValidator.super.initialize(constraintAnnotation);
            this.required = constraintAnnotation.required();
            this.message = constraintAnnotation.message();
            this.values = constraintAnnotation.values();
            this.ignoreCase = constraintAnnotation.ignoreCase();
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
                    constraintValidatorContext.buildConstraintViolationWithTemplate(message).addConstraintViolation();
                    return false;
                }
                return true;
            }
            String str = String.valueOf(requestParam);
            for (String item : values) {
                if (ignoreCase ? item.equalsIgnoreCase(str) : item.equals(str)) {
                    return true;
                }
            }
            // 当返回 false 时，手动设置错误信息
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate(message).addConstraintViolation();
            return false;
        }
    }
}
