package com.github.fanzezhen.fun.framework.core.model.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.text.CharSequenceUtil;
import com.github.fanzezhen.fun.framework.core.model.common.IHolder;
import com.github.fanzezhen.fun.framework.core.model.exception.ServiceException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.ValidationException;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * 参数校验工具类
 * <p>
 * 提供Bean Validation校验、图片格式校验、对象空值判断等功能。
 * 所有校验方法失败时会抛出异常，不会返回布尔值（除isXxx方法外）。
 *
 */
@Slf4j
@SuppressWarnings("unused")
public class ValidUtil {

    public static final String IMG_ERR_MSG = "图片校验不通过！";
    public static final String IMG_PATTERN_ERR_MSG = "图片格式不正确！";

    private ValidUtil() {
    }

    static final String EMPTY_ERROR_MESSAGE = "图片文件不能为空";

    /**
     * 验证对象的约束条件
     *
     * @param bean 要验证的对象
     */
    public static <T> void validate(T bean) {
        validate(bean, CharSequenceUtil.EMPTY, CharSequenceUtil.EMPTY);
    }

    /**
     * 验证对象的约束条件，并添加自定义的错误信息
     *
     * @param bean     要验证的对象
     * @param startMsg 错误信息的前缀
     * @param endMsg   错误信息的后缀
     */
    public static <T> void validate(T bean, String startMsg, String endMsg) {
        Set<ConstraintViolation<T>> violations = loadViolationSet(bean);
        if (CollUtil.isEmpty(violations)) {
            return;
        }
        throw new ConstraintViolationException(startMsg + violations.iterator().next().getMessage() + endMsg, violations);
    }

    /**
     * 加载对象的约束违规集合
     *
     * @param bean 要验证的对象
     *
     * @return 约束违规集合
     */
    public static <T> Set<ConstraintViolation<T>> loadViolationSet(T bean) {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            Validator validator = factory.getValidator();
            return validator.validate(bean);
        }
    }

    /**
     * 验证文件是否为有效的图片
     *
     * @param imageFile 要验证的图片文件
     */
    public static void validateImage(File imageFile) {
        try {
            BufferedImage image = ImageIO.read(imageFile);
            if (isBlank(image)) {
                throw new ServiceException(EMPTY_ERROR_MESSAGE);
            }
        } catch (Exception exception) {
            log.warn(IMG_ERR_MSG, exception);
            throw new ValidationException(IMG_ERR_MSG);
        }
    }

    /**
     * 判断文件是否为图片
     *
     * @param imageFile 要判断的图片文件
     *
     * @return 如果是图片返回true，否则返回false
     */
    public static boolean isImage(File imageFile) {
        try {
            BufferedImage image = ImageIO.read(imageFile);
            if (image != null) {
                return true;
            }
        } catch (Exception e) {
            throw new ServiceException(IMG_PATTERN_ERR_MSG);
        }
        return false;
    }

    /**
     * 验证InputStream是否为有效的图片
     *
     * @param inputStream 要验证的图片输入流
     */
    public static void validateImage(InputStream inputStream) {
        try {
            BufferedImage image = ImageIO.read(inputStream);
            if (isBlank(image)) {
                throw new ServiceException(EMPTY_ERROR_MESSAGE);
            }
        } catch (Exception throwable) {
            log.warn(IMG_ERR_MSG, throwable);
            throw new ValidationException(IMG_ERR_MSG);
        }
    }

    /**
     * 判断InputStream是否为图片
     *
     * @param inputStream 要判断的图片输入流
     *
     * @return 如果是图片返回true，否则返回false
     */
    public static boolean isImage(InputStream inputStream) {
        try {
            BufferedImage image = ImageIO.read(inputStream);
            if (image != null) {
                return true;
            }
        } catch (Exception e) {
            throw new ServiceException(IMG_PATTERN_ERR_MSG);
        }
        return false;
    }

    /**
     * 判断对象是否为空
     * <p>
     * 支持多种类型的空值判断：null、空字符串、空集合、空Map、空数组、IHolder.isEmpty()
     *
     * @param o 待判断的对象
     *
     * @return true表示为空
     */
    public static boolean isEmpty(Object o) {
        return isEmpty(o, false);
    }

    /**
     * 判断对象是否为空（可选是否trim字符串）
     *
     * @param o       待判断的对象
     * @param isStrip 是否对字符串进行trim后再判断（true时" "也会被认为是空）
     *
     * @return true表示为空
     */
    public static boolean isEmpty(Object o, boolean isStrip) {
        if (o == null) {
            return true;
        }
        if (isStrip) {
            if (CharSequenceUtil.isBlank(String.valueOf(o))) {
                return true;
            }
        } else {
            if (CharSequenceUtil.isEmpty(String.valueOf(o))) {
                return true;
            }
        }
        return switch (o) {
            case IHolder holder -> holder.isEmpty();
            case Map<?, ?> map when MapUtil.isEmpty(map) -> true;
            case Collection<?> collection when CollUtil.isEmpty(collection) -> true;
            case byte[] bytes when bytes.length == 0 -> true;
            default -> o instanceof String[] strings && strings.length == 0;
        };
    }

    public static boolean isNotBlank(Object o) {
        return !isBlank(o);
    }

    /**
     * 判断对象是否为空白（会trim字符串）
     * <p>
     * 与isEmpty的区别：会对字符串进行trim，" "会被认为是空
     */
    public static boolean isBlank(Object o) {
        return isEmpty(o, true);
    }
}
