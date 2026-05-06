package com.github.fanzezhen.fun.framework.mp;

import com.github.fanzezhen.fun.framework.mp.generator.Generator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;

/**
 * 代码生成器测试类
 * <p>用于测试MyBatis-Plus代码生成功能，包括实体类、Mapper、Service等文件的自动生成</p>
 */
@Slf4j
@Disabled("默认禁用，需要时手动启用")
class GeneratorTest {

    /**
     * 测试代码生成器功能
     * <p>使用指定的数据库配置和表名生成对应的代码文件</p>
     * <p>注意：此测试默认被禁用，避免意外执行代码生成操作</p>
     */
    @Test
    @Disabled("测试生成器 - 需要时手动启用")
    void testGenerator() {
        // 验证代码生成过程不会抛出异常
        Assertions.assertDoesNotThrow(() ->
            Generator.fastAutoGenerator(Generator.Config.ofTable(
                "jdbc:mysql://192.168.32.5:33200/tmp?serverTimezone=Asia/Shanghai&characterEncoding=UTF-8&useUnicode=true&zeroDateTimeBehavior=convertToNull&useSSL=false&allowMultiQueries=true",
                "qxb-test",
                "ut1zqBo9pLtv_aeY",
                "auth_user", "business_partner"))
        );
    }

}
