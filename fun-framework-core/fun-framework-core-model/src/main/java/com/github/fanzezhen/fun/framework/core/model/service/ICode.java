package com.github.fanzezhen.fun.framework.core.model.service;

public interface ICode {

    /**
     * 获取code
     */
    Integer getCode();

    static <T extends ICode> T toEnum(int code, Class<T> enumClass) {
        for (T enumConstant : enumClass.getEnumConstants()) {
            if (enumConstant.getCode() .equals(code) ) {
                return enumConstant;
            }
        }
        return null;
    }
}
