package com.github.fanzezhen.fun.framework.data.graph.base.template;

import com.github.fanzezhen.fun.framework.core.model.annotation.Entity;
import com.github.fanzezhen.fun.framework.core.model.enums.FunCoreDataExceptionEnum;
import com.github.fanzezhen.fun.framework.core.model.exception.ServiceException;
import com.github.fanzezhen.fun.framework.data.graph.base.annotation.GraphId;
import com.github.fanzezhen.fun.framework.data.graph.base.annotation.GraphNode;
import com.github.fanzezhen.fun.framework.data.graph.base.config.FunGraphProperties;
import lombok.Data;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

/**
 * 图多数据源模板测试
 */
@DisplayName("图多数据源模板")
class BaseMultiDatasourceGraphTemplateTest {

    /**
     * 构造两数据源配置
     *
     * @param defaultDatasource 默认数据源名称
     * @return 图数据库配置
     */
    private static FunGraphProperties twoDatasourceProperties(final String defaultDatasource) {
        final FunGraphProperties properties = new FunGraphProperties();
        properties.setDefaultDatasource(defaultDatasource);
        final FunGraphProperties.Config primary = new FunGraphProperties.Config();
        primary.setName("primary");
        final FunGraphProperties.Config secondary = new FunGraphProperties.Config();
        secondary.setName("secondary");
        properties.setConfigs(List.of(primary, secondary));
        return properties;
    }

    @Test
    @DisplayName("惰性创建：构造期不建任何模板，首次使用才创建")
    void testConstruct_ShouldNotCreateTemplateEagerly() {
        final StubMultiDatasourceTemplate multiTemplate =
            new StubMultiDatasourceTemplate(twoDatasourceProperties("primary"));

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
        final StubMultiDatasourceTemplate multiTemplate =
            new StubMultiDatasourceTemplate(twoDatasourceProperties("primary"));

        assertAll(
            () -> assertSame(multiTemplate.findTemplate("primary"), multiTemplate.findTemplate("primary")),
            () -> assertEquals(1, multiTemplate.createdNameList.size())
        );
    }

    @Test
    @DisplayName("路由：数据源名为空时走默认数据源")
    void testFindTemplate_EmptyDatasource_ShouldUseDefault() {
        final StubMultiDatasourceTemplate multiTemplate =
            new StubMultiDatasourceTemplate(twoDatasourceProperties("secondary"));

        multiTemplate.findTemplate("");

        assertEquals(List.of("secondary"), multiTemplate.createdNameList);
    }

    @Test
    @DisplayName("路由：按实体上的 @Entity(datasource) 选择数据源")
    void testFindTemplate_ByEntityAnnotation_ShouldRouteToDeclaredDatasource() {
        final StubMultiDatasourceTemplate multiTemplate =
            new StubMultiDatasourceTemplate(twoDatasourceProperties("primary"));

        multiTemplate.getById("4:db:1", SecondaryNode.class);

        assertEquals(List.of("secondary"), multiTemplate.createdNameList);
    }

    @Test
    @DisplayName("路由：实体未声明数据源时走默认数据源")
    void testFindTemplate_EntityWithoutDatasource_ShouldUseDefault() {
        final StubMultiDatasourceTemplate multiTemplate =
            new StubMultiDatasourceTemplate(twoDatasourceProperties("primary"));

        multiTemplate.getById("4:db:1", PlainNode.class);

        assertEquals(List.of("primary"), multiTemplate.createdNameList);
    }

    @Test
    @DisplayName("路由：默认数据源未配置时退化为任一可用数据源")
    void testGetDefaultOrAnyTemplate_MissingDefault_ShouldFallbackToAny() {
        final StubMultiDatasourceTemplate multiTemplate =
            new StubMultiDatasourceTemplate(twoDatasourceProperties("not-exists"));

        multiTemplate.getDefaultOrAnyTemplate();

        assertEquals(List.of("primary"), multiTemplate.createdNameList);
    }

    @Test
    @DisplayName("配置校验：数据源名称重复时构造期即失败")
    void testConstruct_DuplicatedDatasourceName_ShouldThrowServiceException() {
        final FunGraphProperties properties = new FunGraphProperties();
        final FunGraphProperties.Config first = new FunGraphProperties.Config();
        first.setName("same");
        final FunGraphProperties.Config second = new FunGraphProperties.Config();
        second.setName("same");
        properties.setConfigs(List.of(first, second));

        final ServiceException exception =
            assertThrows(ServiceException.class, () -> new StubMultiDatasourceTemplate(properties));

        assertEquals(FunCoreDataExceptionEnum.TEMPLATE_IMPL_NAME_DUPLICATED.getCode(), exception.getCode());
    }

    @Test
    @DisplayName("指定不存在的数据源时抛出业务异常")
    void testFindTemplate_UnknownDatasource_ShouldThrowServiceException() {
        final StubMultiDatasourceTemplate multiTemplate =
            new StubMultiDatasourceTemplate(twoDatasourceProperties("primary"));

        final ServiceException exception =
            assertThrows(ServiceException.class, () -> multiTemplate.findTemplate("unknown"));

        assertEquals(FunCoreDataExceptionEnum.TEMPLATE_IMPL_NOT_EXISTS.getCode(), exception.getCode());
    }

    @Test
    @DisplayName("未配置任何数据源时抛出业务异常")
    void testGetDefaultOrAnyTemplate_NoDatasource_ShouldThrowServiceException() {
        final StubMultiDatasourceTemplate multiTemplate =
            new StubMultiDatasourceTemplate(new FunGraphProperties());

        final ServiceException exception =
            assertThrows(ServiceException.class, multiTemplate::getDefaultOrAnyTemplate);

        assertEquals(FunCoreDataExceptionEnum.TEMPLATE_IMPL_CONFIG_NOT_EXISTS.getCode(), exception.getCode());
    }

    /**
     * 多数据源模板桩，记录创建过的数据源名称
     */
    private static final class StubMultiDatasourceTemplate extends BaseMultiDatasourceGraphTemplate {

        /**
         * 已创建模板的数据源名称
         */
        private final List<String> createdNameList = new ArrayList<>();

        private StubMultiDatasourceTemplate(final FunGraphProperties funGraphProperties) {
            super(funGraphProperties, null, null);
        }

        @Override
        protected IGraphTemplate createTemplate(final FunGraphProperties.Config config) {
            createdNameList.add(config.getName());
            return mock(IGraphTemplate.class);
        }
    }

    /**
     * 声明了数据源的节点测试实体
     */
    @Data
    @GraphNode(label = "SecondaryNode")
    @Entity(table = "secondary_node", datasource = "secondary")
    private static class SecondaryNode {

        /**
         * 图库内部标识
         */
        @GraphId
        private String elementId;
    }

    /**
     * 未声明数据源的节点测试实体
     */
    @Data
    @GraphNode(label = "PlainNode")
    private static class PlainNode {

        /**
         * 图库内部标识
         */
        @GraphId
        private String elementId;
    }
}
