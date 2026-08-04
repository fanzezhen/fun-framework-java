package com.github.fanzezhen.fun.framework.core.model.mapper;

import com.github.fanzezhen.fun.framework.core.model.dto.PageDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Orika 引擎适配器测试。
 * <p>
 * 验证 {@link OrikaObjectMapper} 与 MethodHandle 引擎在基础映射语义上一致，
 * 保证 fun.mapper.engine=orika 时行为不回退。
 * </p>
 *
 * @since 4.1.0
 */
class OrikaObjectMapperTest {

    private final FunObjectMapper mapper = new OrikaObjectMapper();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class UserEntity {
        private Long id;
        private String username;
        private Integer age;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class UserBO {
        private Long id;
        private String username;
        private Integer age;
    }

    @Test
    void testMap() {
        UserBO bo = mapper.map(new UserEntity(1L, "张三", 25), UserBO.class);
        assertNotNull(bo);
        assertEquals(1L, bo.getId());
        assertEquals("张三", bo.getUsername());
        assertEquals(25, bo.getAge());
    }

    @Test
    void testMapAsList() {
        List<UserEntity> entities = Arrays.asList(
                new UserEntity(1L, "张三", 25),
                new UserEntity(2L, "李四", 30));
        List<UserBO> bos = mapper.mapAsList(entities, UserBO.class);
        assertEquals(2, bos.size());
        assertEquals("张三", bos.get(0).getUsername());
        assertEquals("李四", bos.get(1).getUsername());
    }

    @Test
    void testMapPage() {
        PageDTO<UserEntity> page = new PageDTO<>();
        page.setCurrent(1);
        page.setSize(10);
        page.setTotal(2L);
        page.setRecords(Arrays.asList(
                new UserEntity(1L, "张三", 25),
                new UserEntity(2L, "李四", 30)));
        PageDTO<UserBO> boPage = mapper.mapPage(page, UserEntity.class, UserBO.class);
        assertNotNull(boPage);
        assertEquals(1, boPage.getCurrent());
        assertEquals(2L, boPage.getTotal());
        assertEquals(2, boPage.getRecords().size());
        assertEquals("张三", boPage.getRecords().get(0).getUsername());
    }
}
