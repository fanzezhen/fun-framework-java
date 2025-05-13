package com.github.fanzezhen.fun.framework.core.web.mvc;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 值域范围校验注册，实现了 spring-hibernate validate
 * ex： @StringsOf(required = true, values = {"SSO", "NATIVE"}, message = "登录模式目前仅支持SSO, NATIVE两种")
 */
@Target({ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = BelongTo.BelongToConstraintValidator.class)
public @interface BelongTo {
    String message() default "参数不合法";

    Class<?>[] groups() default {};

    String[] values();

    /*
    是否忽略大小写
     */
    boolean ignoreCase() default false;

    /**
     * 是否强制需要参数
     */
    boolean required() default false;

    class BelongToConstraintValidator implements ConstraintValidator<BelongTo, Object> {
        private boolean required;
        private String message;
        private String[] values;
        private boolean ignoreCase;

        @Override
        public void initialize(BelongTo constraintAnnotation) {
            ConstraintValidator.super.initialize(constraintAnnotation);
            this.required = constraintAnnotation.required();
            this.message = constraintAnnotation.message();
            this.values = constraintAnnotation.values();
            this.ignoreCase = constraintAnnotation.ignoreCase();
        }

        @Override
        public boolean isValid(Object requestParam, ConstraintValidatorContext constraintValidatorContext) {
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
