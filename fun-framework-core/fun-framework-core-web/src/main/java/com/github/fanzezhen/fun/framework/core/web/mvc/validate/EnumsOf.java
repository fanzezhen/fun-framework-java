package com.github.fanzezhen.fun.framework.core.web.mvc.validate;

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

@Target({ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EnumsOf.EnumCheckValidator.class)
public @interface EnumsOf {
    String message() default "参数不合法";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    Class<? extends Enum<?>> enumClass();

    String checkMethod() default "";

    /**
     * 是否强制需要参数
     */
    boolean required() default false;

    @Slf4j
    class EnumCheckValidator implements ConstraintValidator<EnumsOf, Object> {
        private Class<? extends Enum<?>> enumClass;
        private String checkMethod;
        private boolean required;
        private String message;

        @Override
        public void initialize(EnumsOf annotation) {
            ConstraintValidator.super.initialize(annotation);
            checkMethod = annotation.checkMethod();
            enumClass = annotation.enumClass();
            required = annotation.required();
            message = annotation.message();
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

            boolean result;
            if (designatedMethod()) {
                result = checkByEnumMethod(requestParam);
            } else {
                result = checkByEnumName(requestParam);
            }

            if (!result) {
                constraintValidatorContext.disableDefaultConstraintViolation();
                constraintValidatorContext.buildConstraintViolationWithTemplate(message).addConstraintViolation();
                return false;
            }

            return true;
        }

        private boolean checkByEnumName(Object requestParam) {
            if (!(requestParam instanceof String)) {
                return false;
            }

            String str = (String) requestParam;
            return Arrays.stream(enumClass.getEnumConstants())
                .anyMatch(e -> e.name().equalsIgnoreCase(str));
        }

        private boolean checkByEnumMethod(Object requestParam) {
            try {
                Method method = enumClass.getMethod(checkMethod, requestParam.getClass());

                if (!Boolean.TYPE.equals(method.getReturnType()) && !Boolean.class.equals(method.getReturnType())) {
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

        private boolean designatedMethod() {
            return checkMethod != null && !checkMethod.isEmpty();
        }
    }
}
