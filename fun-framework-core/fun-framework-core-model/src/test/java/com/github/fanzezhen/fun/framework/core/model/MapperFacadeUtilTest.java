package com.github.fanzezhen.fun.framework.core.model;

import com.github.fanzezhen.fun.framework.core.model.dto.PageDTO;
import com.github.fanzezhen.fun.framework.core.model.util.MapperFacadeUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * MapperFacadeUtil 单元测试
 */
class MapperFacadeUtilTest {

    /**
     * 源实体类 - 模拟数据库实体
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class UserEntity {
        private Long id;
        private String username;
        private String password;
        private Integer age;
    }

    /**
     * 目标业务对象 - 模拟业务层 BO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class UserBO {
        private Long id;
        private String username;
        private Integer age;
    }

    @Test
    void testMap_NullSource() {
        // 测试空对象转换
        UserBO result = MapperFacadeUtil.map(null, UserBO.class);
        assertNull(result);
    }

    @Test
    void testMap_ValidSource() {
        // 测试单个对象转换
        UserEntity entity = new UserEntity(1L, "张三", "secret123", 25);

        UserBO bo = com.github.fanzezhen.fun.framework.core.model.util.MapperFacadeUtil.map(entity, UserBO.class);

        assertNotNull(bo);
        assertEquals(1L, bo.getId());
        assertEquals("张三", bo.getUsername());
        assertEquals(25, bo.getAge());
    }

    @Test
    void testMapAsList_NullSource() {
        // 测试空集合转换
        List<UserBO> result = com.github.fanzezhen.fun.framework.core.model.util.MapperFacadeUtil.mapAsList(null, UserBO.class);
        assertNull(result);
    }

    @Test
    void testMapAsList_EmptyList() {
        // 测试空列表转换
        List<UserEntity> entities = Arrays.asList();

        List<UserBO> bos = com.github.fanzezhen.fun.framework.core.model.util.MapperFacadeUtil.mapAsList(entities, UserBO.class);

        assertNotNull(bos);
        assertTrue(bos.isEmpty());
    }

    @Test
    void testMapAsList_ValidList() {
        // 测试集合对象转换
        List<UserEntity> entities = Arrays.asList(
                new UserEntity(1L, "张三", "pwd1", 25),
                new UserEntity(2L, "李四", "pwd2", 30),
                new UserEntity(3L, "王五", "pwd3", 28)
        );

        List<UserBO> bos = com.github.fanzezhen.fun.framework.core.model.util.MapperFacadeUtil.mapAsList(entities, UserBO.class);

        assertNotNull(bos);
        assertEquals(3, bos.size());

        assertEquals(1L, bos.get(0).getId());
        assertEquals("张三", bos.get(0).getUsername());
        assertEquals(25, bos.get(0).getAge());

        assertEquals(2L, bos.get(1).getId());
        assertEquals("李四", bos.get(1).getUsername());
        assertEquals(30, bos.get(1).getAge());
    }

    @Test
    void testPage_NullSource() {
        // 测试空分页对象转换
        PageDTO<UserBO> result = com.github.fanzezhen.fun.framework.core.model.util.MapperFacadeUtil.page(null, UserEntity.class, UserBO.class);
        assertNull(result);
    }

    @Test
    void testPage_EmptyRowList() {
        // 测试空数据列表的分页对象
        PageDTO<UserEntity> entityPage = new PageDTO<>();
        entityPage.setCurrent(1);
        entityPage.setSize(10);
        entityPage.setTotal(0L);
        entityPage.setRecords(Arrays.asList());

        PageDTO<UserBO> boPage = com.github.fanzezhen.fun.framework.core.model.util.MapperFacadeUtil.page(entityPage, UserEntity.class, UserBO.class);

        assertNotNull(boPage);
        assertEquals(1L, boPage.getCurrent());
        assertEquals(10L, boPage.getSize());
        assertEquals(0L, boPage.getTotal());
        assertNotNull(boPage.getRecords());
        assertTrue(boPage.getRecords().isEmpty());
    }

    @Test
    void testPage_ValidPageResult() {
        // 测试分页对象转换
        List<UserEntity> entities = Arrays.asList(
                new UserEntity(1L, "张三", "pwd1", 25),
                new UserEntity(2L, "李四", "pwd2", 30)
        );

        PageDTO<UserEntity> entityPage = new PageDTO<>();
        entityPage.setCurrent(1);
        entityPage.setSize(10);
        entityPage.setTotal(2L);
        entityPage.setTotalTime(0.123);
        entityPage.setRecords(entities);

        PageDTO<UserBO> boPage = com.github.fanzezhen.fun.framework.core.model.util.MapperFacadeUtil.page(entityPage, UserEntity.class, UserBO.class);

        assertNotNull(boPage);
        assertEquals(1L, boPage.getCurrent());
        assertEquals(10L, boPage.getSize());
        assertEquals(2L, boPage.getTotal());
        assertEquals(0.123, boPage.getTotalTime());

        List<UserBO> bos = boPage.getRecords();
        assertNotNull(bos);
        assertEquals(2, bos.size());

        assertEquals(1L, bos.get(0).getId());
        assertEquals("张三", bos.get(0).getUsername());
        assertEquals(25, bos.get(0).getAge());

        assertEquals(2L, bos.get(1).getId());
        assertEquals("李四", bos.get(1).getUsername());
        assertEquals(30, bos.get(1).getAge());
    }

    @Test
    void testPage_PreservesMetadata() {
        // 测试分页元数据保持不变
        PageDTO<UserEntity> entityPage = new PageDTO<>();
        entityPage.setCurrent(3);
        entityPage.setSize(20);
        entityPage.setTotal(100L);
        entityPage.setTotalTime(0.567);
        entityPage.setRecords(Arrays.asList(new UserEntity(1L, "测试", "pwd", 20)));

        PageDTO<UserBO> boPage = com.github.fanzezhen.fun.framework.core.model.util.MapperFacadeUtil.page(entityPage, UserEntity.class, UserBO.class);

        assertEquals(3L, boPage.getCurrent());
        assertEquals(20L, boPage.getSize());
        assertEquals(100L, boPage.getTotal());
        assertEquals(0.567, boPage.getTotalTime());
        assertEquals(5L, boPage.getPageCount()); // (100 / 20) = 5
    }
}
