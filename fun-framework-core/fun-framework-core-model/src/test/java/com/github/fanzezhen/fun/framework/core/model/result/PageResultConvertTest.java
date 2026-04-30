package com.github.fanzezhen.fun.framework.core.model.result;

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
 * @author fanzezhen
 * @since 4.0.5
 */
class PageResultConvertTest {

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
        private String displayName;
    }

    @Test
    void testConvert_WithFunction() {
        // 准备测试数据
        PageResult<SourceData> sourcePage = new PageResult<>();
        sourcePage.setCurrentPage(1L);
        sourcePage.setPageSize(10L);
        sourcePage.setTotal(2L);
        sourcePage.setTotalTime(0.123);
        sourcePage.setRowList(Arrays.asList(
                new SourceData(1L, "张三"),
                new SourceData(2L, "李四")
        ));

        // 使用函数式转换
        Function<SourceData, TargetData> converter = source ->
                new TargetData(source.getId(), source.getName() + "_转换");

        PageResult<TargetData> targetPage = sourcePage.convert(converter);

        // 验证分页元数据
        assertNotNull(targetPage);
        assertEquals(1L, targetPage.getCurrentPage());
        assertEquals(10L, targetPage.getPageSize());
        assertEquals(2L, targetPage.getTotal());
        assertEquals(0.123, targetPage.getTotalTime());

        // 验证转换后的数据
        assertEquals(2, targetPage.getRowList().size());
        assertEquals(1L, targetPage.getRowList().get(0).getId());
        assertEquals("张三_转换", targetPage.getRowList().get(0).getDisplayName());
        assertEquals(2L, targetPage.getRowList().get(1).getId());
        assertEquals("李四_转换", targetPage.getRowList().get(1).getDisplayName());
    }

    @Test
    void testConvert_EmptyRowList() {
        PageResult<SourceData> sourcePage = new PageResult<>();
        sourcePage.setCurrentPage(1L);
        sourcePage.setPageSize(10L);
        sourcePage.setTotal(0L);
        sourcePage.setRowList(Arrays.asList());

        PageResult<TargetData> targetPage = sourcePage.convert(source ->
                new TargetData(source.getId(), source.getName()));

        assertNotNull(targetPage);
        assertEquals(0L, targetPage.getTotal());
        assertNotNull(targetPage.getRowList());
        assertTrue(targetPage.getRowList().isEmpty());
    }

    @Test
    void testConvert_NullRowList() {
        PageResult<SourceData> sourcePage = new PageResult<>();
        sourcePage.setCurrentPage(1L);
        sourcePage.setPageSize(10L);
        sourcePage.setTotal(0L);
        sourcePage.setRowList(null);

        PageResult<TargetData> targetPage = sourcePage.convert(source ->
                new TargetData(source.getId(), source.getName()));

        assertNotNull(targetPage);
        assertNotNull(targetPage.getRowList());
        assertTrue(targetPage.getRowList().isEmpty());
    }

    @Test
    void testConvert_PreservesAllMetadata() {
        // 测试所有分页元数据是否被正确保留
        PageResult<SourceData> sourcePage = new PageResult<>();
        sourcePage.setCurrentPage(5L);
        sourcePage.setPageSize(20L);
        sourcePage.setTotal(100L);
        sourcePage.setTotalTime(0.456);
        sourcePage.setRowList(Arrays.asList(new SourceData(1L, "测试")));

        PageResult<TargetData> targetPage = sourcePage.convert(source ->
                new TargetData(source.getId(), source.getName()));

        assertEquals(5L, targetPage.getCurrentPage());
        assertEquals(20L, targetPage.getPageSize());
        assertEquals(100L, targetPage.getTotal());
        assertEquals(0.456, targetPage.getTotalTime());
        assertEquals(5L, targetPage.getPageCount()); // 100 / 20 = 5
    }

    @Test
    void testConvert_WithMapperObject() {
        // 模拟 MapperFacade 的行为
        Object mockMapper = new Object() {
            @SuppressWarnings("unused")
            public <T, R> java.util.List<R> mapAsList(Iterable<T> source, Class<R> targetClass) {
                java.util.List<R> result = new java.util.ArrayList<>();
                for (T item : source) {
                    if (item instanceof SourceData) {
                        SourceData sourceData = (SourceData) item;
                        @SuppressWarnings("unchecked")
                        R targetData = (R) new TargetData(sourceData.getId(), "映射_" + sourceData.getName());
                        result.add(targetData);
                    }
                }
                return result;
            }
        };

        PageResult<SourceData> sourcePage = new PageResult<>();
        sourcePage.setCurrentPage(1L);
        sourcePage.setPageSize(10L);
        sourcePage.setTotal(2L);
        sourcePage.setRowList(Arrays.asList(
                new SourceData(1L, "张三"),
                new SourceData(2L, "李四")
        ));

        PageResult<TargetData> targetPage = sourcePage.convert(SourceData.class, TargetData.class, mockMapper);

        assertNotNull(targetPage);
        assertEquals(2, targetPage.getRowList().size());
        assertEquals("映射_张三", targetPage.getRowList().get(0).getDisplayName());
        assertEquals("映射_李四", targetPage.getRowList().get(1).getDisplayName());
    }

    @Test
    void testConvert_WithInvalidMapper() {
        // 测试无效的 mapper 对象
        Object invalidMapper = new Object(); // 没有 mapAsList 方法

        PageResult<SourceData> sourcePage = new PageResult<>();
        sourcePage.setRowList(Arrays.asList(new SourceData(1L, "测试")));

        assertThrows(IllegalArgumentException.class, () -> {
            sourcePage.convert(SourceData.class, TargetData.class, invalidMapper);
        });
    }
}
