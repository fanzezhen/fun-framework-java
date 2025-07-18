package com.github.fanzezhen.fun.framework.mp;

import com.github.fanzezhen.fun.framework.mp.generator.Generator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;

@Slf4j
@Disabled
class GeneratorTest {

    @Test
    @Disabled("测试生成器")
    void testGenerator() {
        Assertions.assertDoesNotThrow(() ->
            Generator.fastAutoGenerator(Generator.Config.ofTable(
                "jdbc:mysql://192.168.32.5:33200/tmp?serverTimezone=Asia/Shanghai&characterEncoding=UTF-8&useUnicode=true&zeroDateTimeBehavior=convertToNull&useSSL=false&allowMultiQueries=true",
                "qxb-test",
                "ut1zqBo9pLtv_aeY",
                "auth_user", "business_partner"))
        );
    }

}
