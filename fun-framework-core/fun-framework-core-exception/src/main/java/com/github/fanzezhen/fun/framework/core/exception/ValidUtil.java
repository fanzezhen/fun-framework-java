package com.github.fanzezhen.fun.framework.core.exception;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.ValidationException;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.util.Set;

/**
 * @author fanzezhen
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
     * @param bean 要验证的对象
     */
    public static <T> void validate(T bean) {
        validate(bean, CharSequenceUtil.EMPTY, CharSequenceUtil.EMPTY);
    }

    /**
     * 验证对象的约束条件，并添加自定义的错误信息
     * @param bean 要验证的对象
     * @param startMsg 错误信息的前缀
     * @param endMsg 错误信息的后缀
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
     * @param bean 要验证的对象
     * @return 约束违规集合
     */
    public static <T> Set<ConstraintViolation<T>> loadViolationSet(T bean) {
        try(ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            Validator validator = factory.getValidator();
            return validator.validate(bean);
        }
    }

    /**
     * 验证文件是否为有效的图片
     * @param imageFile 要验证的图片文件
     */
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

    /**
     * 判断文件是否为图片
     * @param imageFile 要判断的图片文件
     * @return 如果是图片返回true，否则返回false
     */
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

    /**
     * 验证MultipartFile是否为有效的图片
     * @param imageMultipartFile 要验证的图片文件
     */
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

    /**
     * 判断MultipartFile是否为图片
     * @param imageMultipartFile 要判断的图片文件
     * @return 如果是图片返回true，否则返回false
     */
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

    /**
     * 验证InputStream是否为有效的图片
     * @param inputStream 要验证的图片输入流
     */
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

    /**
     * 判断InputStream是否为图片
     * @param inputStream 要判断的图片输入流
     * @return 如果是图片返回true，否则返回false
     */
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
