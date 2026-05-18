package com.github.fanzezhen.fun.framework.core.model.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * YApi 模型
 * <p>
 * 用于封装 YApi 接口文档的数据结构，包含接口组名称、描述和接口列表。
 * </p>
 */
@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class YApiModel {
    /**
     * 接口组名称
     */
    private String name;

    /**
     * 接口组描述
     */
    private String desc;

    /**
     * 接口列表
     */
    private List<Api> list;

    /**
     * YApi 接口模型
     */
    @Data
    @Accessors(chain = true)
    public static class Api{
        /**
         * 接口标题
         */
        private String title;

        /**
         * 接口路径
         */
        private String path;
    }
}
