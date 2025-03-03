package com.github.fanzezhen.fun.framework.core;

import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson2.JSON;
import com.github.fanzezhen.fun.framework.core.model.YApiModel;
import com.github.fanzezhen.fun.framework.core.util.YApiUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

@Slf4j
@Disabled("手动触发")
class YApiUtilTest {

    @Test
    @Disabled("手动触发")
     void test() throws IOException {
        String apiJson = FileUtil.readUtf8String("C:\\code\\fanzezhen\\common\\common-core\\src\\test\\java\\com\\github\\fanzezhen\\common\\core\\api.json");
        File file = YApiUtil.jsonToExcel(JSON.parseArray(apiJson, YApiModel.class));
        System.out.println(file.getAbsolutePath());
        Assertions.assertTrue(true);
    }
}
