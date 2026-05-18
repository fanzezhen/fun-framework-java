package com.github.fanzezhen.fun.framework.core.model.common;

import lombok.Data;

import java.awt.image.BufferedImage;
import java.time.LocalDateTime;

/**
 * 图形验证码
 * <p>
 * 封装图形验证码的图片、验证码内容和过期时间。
 * </p>
 */
@Data
public class ImageCode {

    /**
     * 验证码图片
     */
    private BufferedImage image;

    /**
     * 验证码内容
     */
    private String code;

    /**
     * 过期时间
     */
    private LocalDateTime expireTime;

    /**
     * 构造图形验证码
     *
     * @param image    验证码图片
     * @param code     验证码内容
     * @param expireIn 有效期（秒）
     */
    public ImageCode(BufferedImage image, String code, int expireIn) {
        this.image = image;
        this.code = code;
        this.expireTime = LocalDateTime.now().plusSeconds(expireIn);
    }

    /**
     * 判断验证码是否已过期
     *
     * @return true 表示已过期，false 表示未过期
     */
    public boolean isExpire() {
        return LocalDateTime.now().isAfter(expireTime);
    }
}
