package com.github.fanzezhen.fun.framework.core.exception;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.validation.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.util.Set;

/**
 * @author zezhen.fan
 */
@Slf4j
public class ValidUtil {

    public static final String IMG_ERR_MSG = "图片校验不通过！";
    public static final String IMG_PATTERN_ERR_MSG = "图片格式不正确！";

    private ValidUtil() {
    }
    static final String EMPTY_ERROR_MESSAGE = "图片文件不能为空";

    public static <T> void validate(T bean) {
        validate(bean, CharSequenceUtil.EMPTY, CharSequenceUtil.EMPTY);
    }

    public static <T> void validate(T bean, String startMsg, String endMsg) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<T>> violations = validator.validate(bean);
        if (CollUtil.isEmpty(violations)) {
            return;
        }
        throw new ConstraintViolationException(startMsg + violations.iterator().next().getMessage() + endMsg, violations);
    }

    public static <T> Set<ConstraintViolation<T>> loadViolationSet(T bean) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        return validator.validate(bean);
    }

    public static <T> void throwInValidate(T bean) {
        Set<ConstraintViolation<T>> violations = loadViolationSet(bean);
        if (CollUtil.isEmpty(violations)) {
            return;
        }
        throw new ConstraintViolationException(violations.iterator().next().getMessage(), violations);
    }

    public static void validateImage(File imageFile) {
        try {
            BufferedImage image = ImageIO.read(imageFile);
            if (ExceptionUtil.isBlank(image)){
                throw ExceptionUtil.wrapException(EMPTY_ERROR_MESSAGE);
            }
        } catch (Exception exception) {
            log.warn(IMG_ERR_MSG, exception);
            throw new ValidationException(IMG_ERR_MSG);
        }
    }

    public static boolean isImage(File imageFile) {
        try {
            BufferedImage image = ImageIO.read(imageFile);
            if (image != null) {
                return true;
            }
        } catch (Exception e) {
            throw ExceptionUtil.wrapException(IMG_PATTERN_ERR_MSG);
        }
        return false;
    }

    public static void validateImage(MultipartFile imageMultipartFile) {
        try {
            BufferedImage image = ImageIO.read(imageMultipartFile.getInputStream());
            if (ExceptionUtil.isBlank(image)){
                throw ExceptionUtil.wrapException(EMPTY_ERROR_MESSAGE);
            }
        } catch (Exception throwable) {
            log.warn(IMG_ERR_MSG, throwable);
            throw new ValidationException(IMG_ERR_MSG);
        }
    }

    public static boolean isImage(MultipartFile imageMultipartFile) {
        try {
            BufferedImage image = ImageIO.read(imageMultipartFile.getInputStream());
            if (image != null) {
                return true;
            }
        } catch (Exception e) {
            throw ExceptionUtil.wrapException(IMG_PATTERN_ERR_MSG);
        }
        return false;
    }

    public static void validateImage(InputStream inputStream) {
        try {
            BufferedImage image = ImageIO.read(inputStream);
            if (ExceptionUtil.isBlank(image)){
                throw ExceptionUtil.wrapException(EMPTY_ERROR_MESSAGE);
            }
        } catch (Exception throwable) {
            log.warn(IMG_ERR_MSG, throwable);
            throw new ValidationException(IMG_ERR_MSG);
        }
    }

    public static boolean isImage(InputStream inputStream) {
        try {
            BufferedImage image = ImageIO.read(inputStream);
            if (image != null) {
                return true;
            }
        } catch (Exception e) {
            throw ExceptionUtil.wrapException(IMG_PATTERN_ERR_MSG);
        }
        return false;
    }
}
