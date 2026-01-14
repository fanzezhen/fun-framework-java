package com.github.fanzezhen.fun.framework.core.data;

import com.github.fanzezhen.fun.framework.core.data.util.StrFileUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Slf4j
@Disabled
class StrFileUtilTest {

    @Test
    @Disabled("手动使用")
    void testTrimLines() {
        String filePath = System.getProperty("user.dir") + "\\fun-framework-core-data\\src\\test\\resources\\name.csv";
        Assertions.assertDoesNotThrow(() -> StrFileUtil.trimLines(filePath, "\""));
    }
}
