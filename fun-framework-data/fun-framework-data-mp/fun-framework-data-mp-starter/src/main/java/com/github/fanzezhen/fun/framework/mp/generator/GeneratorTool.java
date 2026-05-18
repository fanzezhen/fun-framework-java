package com.github.fanzezhen.fun.framework.mp.generator;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.ArrayUtil;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.*;

import java.io.File;
import java.util.Collections;

/**
 * 代码生成器工具类
 * <p>
 * 提供基于 MyBatis-Plus Generator 的代码生成功能，支持从数据库表生成 Entity、Mapper、Service、Controller 等代码。
 * <p>
 * 使用示例：
 * <pre>{@code
 * GeneratorBean bean = new GeneratorBean();
 * bean.setDbUrl("jdbc:mysql://localhost:3306/demo");
 * bean.setDbUsername("root");
 * bean.setDbPassword("password");
 * bean.setTableNameList(Arrays.asList("user", "role"));
 * GeneratorTool.generator(bean);
 * }</pre>
 */
public final class GeneratorTool {
    /**
     * 私有构造函数，防止实例化
     */
    private GeneratorTool() {
    }

    /**
     * 执行代码生成
     * <p>
     * 根据 {@link GeneratorBean} 配置从数据库表生成代码。
     * 如果配置中未指定数据库连接信息，将从控制台读取。
     *
     * @param generatorBean 生成器配置对象
     */
    public static void generator(final GeneratorBean generatorBean) {
        String dbUrl = CharSequenceUtil.isBlank(generatorBean.getDbUrl()) ? generatorBean.scanner("数据库链接（如：jdbc:mysql://localhost:3306/demo）") : generatorBean.getDbUrl();
        String dbUsername = CharSequenceUtil.isBlank(generatorBean.getDbUsername()) ? generatorBean.scanner("数据库用户名称") : generatorBean.getDbUsername();
        String dbPassword = CharSequenceUtil.isBlank(generatorBean.getDbPassword()) ? generatorBean.scanner("数据库用户密码") : generatorBean.getDbPassword();
        FastAutoGenerator generator = FastAutoGenerator.create(dbUrl, dbUsername, dbPassword);
        generator.globalConfig(builder -> builder
                // 设置作者
                .author(System.getProperty("user.name"))
                // 开启 swagger 模式
//                .enableSwagger()
                // 生成结束时不打开文件管理器
                .disableOpenDir()
                // 指定输出目录
                .outputDir(System.getProperty("user.dir") + File.separator + generatorBean.getJavaDir()))
                .packageConfig(builder -> builder
                        // 设置父包名
                        .parent(generatorBean.getParentPackageName())
                        // 设置mapperXml生成路径
                        .pathInfo(Collections.singletonMap(OutputFile.xml, System.getProperty("user.dir") + StrPool.SLASH + generatorBean.getMapperDir())))
                .strategyConfig(builder -> {
                    // 设置需要生成的表名
                    builder.addInclude(generatorBean.getTableNameList());
                    if (ArrayUtil.isNotEmpty(generatorBean.getIgnoreTablePrefix())) {
                        // 设置过滤表前缀
                        builder.addTablePrefix(generatorBean.getIgnoreTablePrefix());
                    }
                    builder.entityBuilder()
                            .superClass(generatorBean.getSuperEntityClass())
                            .formatFileName(generatorBean.getFormatFileName())
                            .logicDeleteColumnName(generatorBean.getLogicDeleteColumnName())
                            .enableLombok().enableChainModel().enableTableFieldAnnotation();
                })
                // 使用Freemarker引擎模板，默认的是Velocity引擎模板
//                .templateEngine(new FreemarkerTemplateEngine())
                .execute();
    }

}
