package com.github.fanzezhen.fun.framework.mp.tenant;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import com.github.fanzezhen.fun.framework.core.context.ContextHolder;
import lombok.Data;
import org.apache.ibatis.annotations.Mapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 多租户隔离端到端测试
 * <p>
 * 拉起真实的 MyBatis-Plus + H2，验证「租户条件确实拼进 SQL」这一最终效果：单元测试只能证明
 * 行处理器返回了什么，证不了拦截器是否被装配、条件是否落到 SQL 上。
 * </p>
 *
 * @since 4.1.1
 */
@DisplayName("多租户隔离端到端")
@SpringBootTest(properties = {
        "spring.main.web-application-type=none",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.url=jdbc:h2:mem:isolation;MODE=MySQL;DB_CLOSE_DELAY=-1;DATABASE_TO_UPPER=false",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.sql.init.mode=always",
        "spring.sql.init.schema-locations=classpath:db/tenant-schema-h2.sql",
        "fun.mp.tenant.enabled=true",
        "fun.mp.tenant.value-type=long",
        "fun.mp.tenant.default-tenant-id=0",
})
class TenantIsolationIntegrationTest {

    /**
     * 租户表 mapper
     */
    @Autowired
    private OrderMapper orderMapper;

    /**
     * 全局表 mapper
     */
    @Autowired
    private DictMapper dictMapper;

    /**
     * 清表用，走 JDBC 绕开租户拦截器，避免清理动作自身被加上租户条件
     */
    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 主拦截器，用于验证租户拦截器已被收编且顺序正确
     */
    @Autowired
    private MybatisPlusInterceptor mybatisPlusInterceptor;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("DELETE FROM biz_order");
    }

    @AfterEach
    void tearDown() {
        ContextHolder.clean();
        TenantIgnoreContext.clear();
    }

    @Test
    @DisplayName("装配_租户拦截器_收编进主拦截器且排在分页之前")
    void assemble_TenantInterceptor_ShouldPrecedePagination() {
        List<String> chain = mybatisPlusInterceptor.getInterceptors().stream()
                .map(interceptor -> interceptor.getClass().getSimpleName())
                .toList();

        assertAll(
                () -> assertTrue(chain.contains(TenantLineInnerInterceptor.class.getSimpleName()),
                        "租户拦截器须被收编进插件链，否则租户条件不会落到 SQL 上"),
                () -> assertEquals(0, chain.indexOf(TenantLineInnerInterceptor.class.getSimpleName()),
                        "须排在无 @Order 的分页拦截器之前，否则分页 count 会越权计数")
        );
    }

    @Test
    @DisplayName("查询_含租户列的表_只可见本租户数据")
    void select_TenantTable_ShouldOnlySeeOwnRows() {
        ContextHolder.setTenantId("1001");
        insert("A-1001");
        ContextHolder.setTenantId("1002");
        insert("A-1002");

        ContextHolder.setTenantId("1001");
        List<OrderEntity> visible = orderMapper.selectList(null);

        assertAll(
                () -> assertEquals(1, visible.size(), "只应看到本租户的数据"),
                () -> assertEquals("A-1001", visible.get(0).getOrderNo()),
                () -> assertEquals(1001L, visible.get(0).getTenantId())
        );
    }

    @Test
    @DisplayName("插入_含租户列的表_自动填充当前租户")
    void insert_TenantTable_ShouldFillCurrentTenant() {
        ContextHolder.setTenantId("2001");
        insert("B-2001");

        TenantIgnoreContext.set(true);
        List<OrderEntity> all = orderMapper.selectList(null);
        TenantIgnoreContext.clear();

        assertTrue(all.stream().anyMatch(o -> "B-2001".equals(o.getOrderNo()) && o.getTenantId() == 2001L),
                "未显式赋值 tenantId，应由拦截器补上当前租户");
    }

    @Test
    @DisplayName("查询_无租户列的全局表_不受租户过滤")
    void select_GlobalTable_ShouldNotBeFiltered() {
        ContextHolder.setTenantId("3001");
        List<DictEntity> underTenant = dictMapper.selectList(null);
        ContextHolder.setTenantId("3002");
        List<DictEntity> underOtherTenant = dictMapper.selectList(null);

        assertAll(
                () -> assertEquals(2, underTenant.size(), "全局表应对所有租户可见"),
                () -> assertEquals(2, underOtherTenant.size())
        );
    }

    @Test
    @DisplayName("查询_逃生口开启_可见全部租户数据")
    void select_IgnoreContextOn_ShouldSeeAllTenants() {
        ContextHolder.setTenantId("4001");
        insert("C-4001");
        ContextHolder.setTenantId("4002");
        insert("C-4002");

        TenantIgnoreContext.set(true);
        List<OrderEntity> all = orderMapper.selectList(null);

        assertEquals(2, all.size(), "跨租户上下文下应看到两个租户的数据");
    }

    /**
     * 插入一条订单，不显式赋值租户列
     *
     * @param orderNo 订单号
     */
    private void insert(final String orderNo) {
        OrderEntity entity = new OrderEntity();
        entity.setOrderNo(orderNo);
        orderMapper.insert(entity);
    }

    /**
     * 测试装配
     * <p>
     * 分页等 {@code InnerInterceptor} 由 test 域既有的 {@code MybatisPlusExampleConfig} 经
     * {@code FunMpAutoConfiguration} 的组件扫描提供——其分页拦截器不标 {@code @Order}，
     * 正是用于验证租户拦截器（{@code @Order(0)}）排在其之前的场景。
     * </p>
     */
    @SpringBootConfiguration
    @EnableAutoConfiguration
    @MapperScan("com.github.fanzezhen.fun.framework.mp.tenant")
    static class TestConfig {
    }

    /**
     * 租户表实体
     */
    @Data
    @TableName("biz_order")
    public static class OrderEntity {

        /**
         * 主键
         */
        private Long id;

        /**
         * 租户 ID
         */
        private Long tenantId;

        /**
         * 订单号
         */
        private String orderNo;
    }

    /**
     * 全局表实体
     */
    @Data
    @TableName("sys_dict")
    public static class DictEntity {

        /**
         * 主键
         */
        private Long id;

        /**
         * 字典键
         */
        private String dictKey;
    }

    /**
     * 租户表 mapper
     */
    @Mapper
    public interface OrderMapper extends BaseMapper<OrderEntity> {
    }

    /**
     * 全局表 mapper
     */
    @Mapper
    public interface DictMapper extends BaseMapper<DictEntity> {
    }
}
