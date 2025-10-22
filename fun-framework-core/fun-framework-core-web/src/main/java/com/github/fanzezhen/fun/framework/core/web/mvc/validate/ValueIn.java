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
@Constraint(validatedBy = ValueIn.EnumOrValuesValidator.class)
public @interface ValueIn {
    String message() default "参数不合法";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    // 字符串值域校验
    String[] values() default {};

    // 枚举类校验
    Class<? extends Enum<?>> enumClass() default NoneEnum.class;
    
    // 枚举校验方法
    String checkMethod() default "";

    // 是否忽略大小写（仅对字符串值域有效）
    boolean ignoreCase() default false;

    // 是否强制需要参数
    boolean required() default false;
    
    // 占位枚举类，用于表示未设置enumClass的情况
    enum NoneEnum {}
    
    @Slf4j
    class EnumOrValuesValidator implements ConstraintValidator<ValueIn, Object> {
        private boolean required;
        private String message;
        private String[] values;
        private boolean ignoreCase;
        private Class<? extends Enum<?>> enumClass;
        private String checkMethod;

        @Override
        public void initialize(ValueIn constraintAnnotation) {
            ConstraintValidator.super.initialize(constraintAnnotation);
            this.required = constraintAnnotation.required();
            this.message = constraintAnnotation.message();
            this.values = constraintAnnotation.values();
            this.ignoreCase = constraintAnnotation.ignoreCase();
            this.enumClass = constraintAnnotation.enumClass();
            this.checkMethod = constraintAnnotation.checkMethod();
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
                constraintValidatorContext.buildConstraintViolationWithTemplate(message).addConstraintViolation();
                return false;
            }

            return true;
        }

        private boolean validateValues(Object requestParam) {
            String str = String.valueOf(requestParam);
            for (String item : values) {
                if (ignoreCase ? item.equalsIgnoreCase(str) : item.equals(str)) {
                    return true;
                }
            }
            return false;
        }

        private boolean validateEnum(Object requestParam) {
            if (designatedMethod()) {
                return checkByEnumMethod(requestParam);
            } else {
                return checkByEnumName(requestParam);
            }
        }

        private boolean checkByEnumName(Object requestParam) {
            if (!(requestParam instanceof String str)) {
                return false;
            }

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
