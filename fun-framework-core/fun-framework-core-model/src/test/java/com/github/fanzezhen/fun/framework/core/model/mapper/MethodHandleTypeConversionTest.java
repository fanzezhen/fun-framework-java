package com.github.fanzezhen.fun.framework.core.model.mapper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * MethodHandle 引擎异构类型转换测试。
 * <p>
 * 验证从 Orika 迁移到 MethodHandle 引擎后，常见基础类型互转行为不回退。
 * </p>
 *
 * @since 4.1.0
 */
class MethodHandleTypeConversionTest {

    private final FunObjectMapper mapper = new MethodHandleObjectMapper();

    enum Status {
        ACTIVE, INACTIVE
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class Source {
        private Integer intToLong;
        private Long longToInt;
        private String strToInt;
        private Integer intToStr;
        private String strToEnum;
        private Status enumToStr;
        private int primToBox;
        private Integer boxToPrim;
        private String strToBool;
        private Integer numToBool;
        private Date dateToLdt;
        private String onlyInSource;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class Target {
        private Long intToLong;
        private Integer longToInt;
        private Integer strToInt;
        private String intToStr;
        private Status strToEnum;
        private String enumToStr;
        private Integer primToBox;
        private int boxToPrim;
        private Boolean strToBool;
        private Boolean numToBool;
        private LocalDateTime dateToLdt;
    }

    @Test
    void testNumericConversion() {
        Source s = new Source();
        s.setIntToLong(100);
        s.setLongToInt(200L);
        Target t = mapper.map(s, Target.class);
        assertEquals(100L, t.getIntToLong());
        assertEquals(200, t.getLongToInt());
    }

    @Test
    void testStringNumberConversion() {
        Source s = new Source();
        s.setStrToInt("42");
        s.setIntToStr(99);
        Target t = mapper.map(s, Target.class);
        assertEquals(42, t.getStrToInt());
        assertEquals("99", t.getIntToStr());
    }

    @Test
    void testEnumConversion() {
        Source s = new Source();
        s.setStrToEnum("ACTIVE");
        s.setEnumToStr(Status.INACTIVE);
        Target t = mapper.map(s, Target.class);
        assertEquals(Status.ACTIVE, t.getStrToEnum());
        assertEquals("INACTIVE", t.getEnumToStr());
    }

    @Test
    void testBoxingUnboxing() {
        Source s = new Source();
        s.setPrimToBox(7);
        s.setBoxToPrim(8);
        Target t = mapper.map(s, Target.class);
        assertEquals(7, t.getPrimToBox());
        assertEquals(8, t.getBoxToPrim());
    }

    @Test
    void testBooleanConversion() {
        Source s = new Source();
        s.setStrToBool("true");
        s.setNumToBool(1);
        Target t = mapper.map(s, Target.class);
        assertTrue(t.getStrToBool());
        assertTrue(t.getNumToBool());
    }

    @Test
    void testDateConversion() {
        Source s = new Source();
        Date now = new Date(1_700_000_000_000L);
        s.setDateToLdt(now);
        Target t = mapper.map(s, Target.class);
        LocalDateTime expected = now.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        assertEquals(expected, t.getDateToLdt());
    }

    @Test
    void testNullFieldStaysNull() {
        Source s = new Source();
        s.setIntToLong(null);
        Target t = mapper.map(s, Target.class);
        assertNull(t.getIntToLong());
    }
}
