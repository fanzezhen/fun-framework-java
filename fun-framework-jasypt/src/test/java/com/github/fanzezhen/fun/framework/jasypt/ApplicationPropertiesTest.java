package com.github.fanzezhen.fun.framework.jasypt;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 加密组件
 */
@Slf4j
@SpringBootTest
class ApplicationPropertiesTest {
    @Value("${fun.jasypt.test.password}")
    private String password;

    @Test
    void test() {
        Assertions.assertEquals("password", password);
    }

}
