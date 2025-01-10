package com.github.fanzezhen.fun.framework.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author zezhen.fan
 * Desc:
 */
@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class YApiModel {
    private String name;
    private String desc;
    private List<Api> list;

    @Data
    @Accessors(chain = true)
    public static class Api{
        private String title;
        private String path;
    }
}
