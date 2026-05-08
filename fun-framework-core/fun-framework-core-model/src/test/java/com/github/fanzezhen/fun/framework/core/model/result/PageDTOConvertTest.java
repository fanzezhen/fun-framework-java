package com.github.fanzezhen.fun.framework.core.model.result;

import com.github.fanzezhen.fun.framework.core.model.dto.PageDTO;
import com.github.fanzezhen.fun.framework.core.model.util.MapperFacadeUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

/**
 * PageResult.convert() 方法单元测试
 *
 * @since 4.0.5
 */
class PageDTOConvertTest {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class SourceData {
        private Long id;
        private String name;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class TargetData {
        private Long id;
        private String name;
    }

    @Test
    void testConvert_WithFunction() {
        // 准备测试数据
        PageDTO<SourceData> sourcePage = new PageDTO<>();
        sourcePage.setCurrent(1);
        sourcePage.setSize(10);
        sourcePage.setTotal(2L);
        sourcePage.setTotalTime(0.123);
        sourcePage.setRecords(Arrays.asList(
                new SourceData(1L, "张三"),
                new SourceData(2L, "李四")
        ));

        // 使用函数式转换
        Function<SourceData, TargetData> converter = source ->
                new TargetData(source.getId(), source.getName() + "_转换");

        PageDTO<TargetData> targetPage = sourcePage.convert(converter);

        // 验证分页元数据
        assertNotNull(targetPage);
        assertEquals(1L, targetPage.getCurrent());
        assertEquals(10L, targetPage.getSize());
        assertEquals(2L, targetPage.getTotal());
        assertEquals(0.123, targetPage.getTotalTime());

        // 验证转换后的数据
        assertEquals(2, targetPage.getRecords().size());
        assertEquals(1L, targetPage.getRecords().get(0).getId());
        assertEquals("张三_转换", targetPage.getRecords().get(0).getName());
        assertEquals(2L, targetPage.getRecords().get(1).getId());
        assertEquals("李四_转换", targetPage.getRecords().get(1).getName());
    }

    @Test
    void testConvert_EmptyRowList() {
        PageDTO<SourceData> sourcePage = new PageDTO<>();
        sourcePage.setCurrent(1);
        sourcePage.setSize(10);
        sourcePage.setTotal(0L);
        sourcePage.setRecords(Arrays.asList());

        PageDTO<TargetData> targetPage = sourcePage.convert(source ->
                new TargetData(source.getId(), source.getName()));

        assertNotNull(targetPage);
        assertEquals(0L, targetPage.getTotal());
        assertNotNull(targetPage.getRecords());
        assertTrue(targetPage.getRecords().isEmpty());
    }

    @Test
    void testConvert_NullRowList() {
        PageDTO<SourceData> sourcePage = new PageDTO<>();
        sourcePage.setCurrent(1);
        sourcePage.setSize(10);
        sourcePage.setTotal(0L);
        sourcePage.setRecords(null);

        PageDTO<TargetData> targetPage = sourcePage.convert(source ->
                new TargetData(source.getId(), source.getName()));

        assertNotNull(targetPage);
        assertNotNull(targetPage.getRecords());
        assertTrue(targetPage.getRecords().isEmpty());
    }

    @Test
    void testConvert_PreservesAllMetadata() {
        // 测试所有分页元数据是否被正确保留
        PageDTO<SourceData> sourcePage = new PageDTO<>();
        sourcePage.setCurrent(5);
        sourcePage.setSize(20);
        sourcePage.setTotal(100L);
        sourcePage.setTotalTime(0.456);
        sourcePage.setRecords(Arrays.asList(new SourceData(1L, "测试")));

        PageDTO<TargetData> targetPage = sourcePage.convert(source ->
                new TargetData(source.getId(), source.getName()));

        assertEquals(5L, targetPage.getCurrent());
        assertEquals(20L, targetPage.getSize());
        assertEquals(100L, targetPage.getTotal());
        assertEquals(0.456, targetPage.getTotalTime());
        assertEquals(5L, targetPage.getPageCount()); // 100 / 20 = 5
    }

    @Test
    void testConvert_WithMapperObject() {
        PageDTO<SourceData> sourcePage = new PageDTO<>();
        sourcePage.setCurrent(1);
        sourcePage.setSize(10);
        sourcePage.setTotal(2L);
        sourcePage.setRecords(Arrays.asList(
                new SourceData(1L, "张三"),
                new SourceData(2L, "李四")
        ));
        PageDTO<TargetData> targetPage = MapperFacadeUtil.page(sourcePage, SourceData.class, TargetData.class);

        assertNotNull(targetPage);
        assertEquals(2, targetPage.getRecords().size());
        assertEquals("张三", targetPage.getRecords().get(0).getName());
        assertEquals("李四", targetPage.getRecords().get(1).getName());
    }
}
