package com.github.fanzezhen.fun.framework.core.model.template;

import com.github.fanzezhen.fun.framework.core.model.annotation.Entity;
import com.github.fanzezhen.fun.framework.core.model.enums.FunCoreDataExceptionEnum;
import com.github.fanzezhen.fun.framework.core.model.exception.ServiceException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 多数据源模板抽象基类测试
 */
@DisplayName("多数据源模板抽象基类")
class BaseMultiDatasourceTemplateTest {

    /**
     * 存储类型标识
     */
    private static final String MARK = "（test）";

    /**
     * 构造两数据源模板桩
     *
     * @param defaultDatasource 默认数据源名称
     * @return 模板桩
     */
    private static StubTemplate twoDatasourceTemplate(final String defaultDatasource) {
        return new StubTemplate(defaultDatasource, List.of(new StubConfig("primary"), new StubConfig("secondary")));
    }

    @Test
    @DisplayName("惰性创建：构造期不建子模板，首次使用才创建")
    void testConstruct_ShouldNotCreateTemplateEagerly() {
        final StubTemplate multiTemplate = twoDatasourceTemplate("primary");

        assertAll(
            () -> assertTrue(multiTemplate.createdNameList.isEmpty()),
            () -> assertEquals(List.of("primary", "secondary"), multiTemplate.listDatasourceNames())
        );

        multiTemplate.findTemplate("primary");

        assertEquals(List.of("primary"), multiTemplate.createdNameList);
    }

    @Test
    @DisplayName("惰性创建：同一数据源重复取用复用同一实例")
    void testFindTemplate_SameDatasource_ShouldReuseInstance() {
        final StubTemplate multiTemplate = twoDatasourceTemplate("primary");

        assertAll(
            () -> assertSame(multiTemplate.findTemplate("primary"), multiTemplate.findTemplate("primary")),
            () -> assertEquals(1, multiTemplate.createdNameList.size())
        );
    }

    @Test
    @DisplayName("预创建：initAllTemplates 建齐全部子模板，供需启动即创建的实现使用")
    void testInitAllTemplates_ShouldCreateEveryTemplate() {
        final StubTemplate multiTemplate = twoDatasourceTemplate("primary");

        multiTemplate.initAllTemplates();

        assertEquals(List.of("primary", "secondary"), multiTemplate.createdNameList);
    }

    @Test
    @DisplayName("路由：数据源名为空时走默认数据源")
    void testFindTemplate_EmptyDatasource_ShouldUseDefault() {
        final StubTemplate multiTemplate = twoDatasourceTemplate("secondary");

        multiTemplate.findTemplate("");

        assertEquals(List.of("secondary"), multiTemplate.createdNameList);
    }

    @Test
    @DisplayName("路由：按实体上的 @Entity(datasource) 选择数据源")
    void testFindTemplate_ByEntityAnnotation_ShouldRouteToDeclaredDatasource() {
        final StubTemplate multiTemplate = twoDatasourceTemplate("primary");

        multiTemplate.findTemplate(SecondaryEntity.class);

        assertEquals(List.of("secondary"), multiTemplate.createdNameList);
    }

    @Test
    @DisplayName("路由：实体未声明数据源时走默认数据源")
    void testFindTemplate_EntityWithoutDatasource_ShouldUseDefault() {
        final StubTemplate multiTemplate = twoDatasourceTemplate("primary");

        multiTemplate.findTemplate(PlainEntity.class);

        assertEquals(List.of("primary"), multiTemplate.createdNameList);
    }

    @Test
    @DisplayName("路由：默认数据源未配置时退化为任一可用数据源")
    void testGetDefaultOrAnyTemplate_MissingDefault_ShouldFallbackToAny() {
        final StubTemplate multiTemplate = twoDatasourceTemplate("not-exists");

        multiTemplate.getDefaultOrAnyTemplate();

        assertEquals(List.of("primary"), multiTemplate.createdNameList);
    }

    @Test
    @DisplayName("配置校验：数据源名称重复时构造期即失败")
    void testConstruct_DuplicatedName_ShouldThrowServiceException() {
        final List<StubConfig> configs = List.of(new StubConfig("same"), new StubConfig("same"));

        final ServiceException exception =
            assertThrows(ServiceException.class, () -> new StubTemplate("same", configs));

        assertEquals(FunCoreDataExceptionEnum.TEMPLATE_IMPL_NAME_DUPLICATED.getCode(), exception.getCode());
    }

    @Test
    @DisplayName("指定不存在的数据源时抛出业务异常")
    void testFindTemplate_UnknownDatasource_ShouldThrowServiceException() {
        final StubTemplate multiTemplate = twoDatasourceTemplate("primary");

        final ServiceException exception =
            assertThrows(ServiceException.class, () -> multiTemplate.findTemplate("unknown"));

        assertEquals(FunCoreDataExceptionEnum.TEMPLATE_IMPL_NOT_EXISTS.getCode(), exception.getCode());
    }

    @Test
    @DisplayName("未配置任何数据源时抛出业务异常")
    void testGetDefaultOrAnyTemplate_NoDatasource_ShouldThrowServiceException() {
        final StubTemplate multiTemplate = new StubTemplate("default", null);

        final ServiceException exception =
            assertThrows(ServiceException.class, multiTemplate::getDefaultOrAnyTemplate);

        assertEquals(FunCoreDataExceptionEnum.TEMPLATE_IMPL_CONFIG_NOT_EXISTS.getCode(), exception.getCode());
    }

    @Test
    @DisplayName("子模板创建返回 null 时抛出业务异常")
    void testObtainTemplate_CreateReturnsNull_ShouldThrowServiceException() {
        final StubTemplate multiTemplate =
            new StubTemplate("primary", List.of(new StubConfig("primary")), true);

        final ServiceException exception =
            assertThrows(ServiceException.class, () -> multiTemplate.findTemplate("primary"));

        assertEquals(FunCoreDataExceptionEnum.TEMPLATE_IMPL_CREATE_NULL.getCode(), exception.getCode());
    }

    @Test
    @DisplayName("已创建子模板集合只含用过的数据源")
    void testListCreatedTemplates_ShouldOnlyContainUsedDatasources() {
        final StubTemplate multiTemplate = twoDatasourceTemplate("primary");

        multiTemplate.findTemplate("secondary");

        assertEquals(1, multiTemplate.listCreatedTemplates().size());
    }

    /**
     * 数据源配置桩
     *
     * @param name 数据源名称
     */
    private record StubConfig(String name) implements IDatasourceConfig {

        @Override
        public String getName() {
            return name;
        }
    }

    /**
     * 多数据源模板桩，记录创建过的数据源名称
     */
    private static final class StubTemplate extends BaseMultiDatasourceTemplate<Object, StubConfig> {

        /**
         * 已创建子模板的数据源名称
         */
        private final List<String> createdNameList = new ArrayList<>();

        /**
         * 是否令创建返回 null
         */
        private final boolean createNull;

        private StubTemplate(final String defaultDatasource, final List<StubConfig> configs) {
            this(defaultDatasource, configs, false);
        }

        private StubTemplate(final String defaultDatasource,
                             final List<StubConfig> configs,
                             final boolean createNull) {
            super(defaultDatasource, configs, MARK);
            this.createNull = createNull;
        }

        @Override
        protected Object createTemplate(final StubConfig config) {
            createdNameList.add(config.getName());
            return createNull ? null : new Object();
        }
    }

    /**
     * 声明了数据源的实体
     */
    @Entity(table = "secondary_entity", datasource = "secondary")
    private static class SecondaryEntity {
    }

    /**
     * 未声明数据源的实体
     */
    @Entity(table = "plain_entity")
    private static class PlainEntity {
    }
}
