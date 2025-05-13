package com.github.fanzezhen.fun.framework.mp.generator;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.ReflectUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import com.baomidou.mybatisplus.generator.config.rules.IColumnType;
import com.github.fanzezhen.fun.framework.core.data.StringUtil;
import com.github.fanzezhen.fun.framework.mp.base.IBaseMapper;
import com.github.fanzezhen.fun.framework.mp.base.IService;
import com.github.fanzezhen.fun.framework.mp.base.ServiceImpl;
import com.github.fanzezhen.fun.framework.mp.base.entity.BaseEntity;
import com.github.fanzezhen.fun.framework.mp.base.entity.BaseGenericEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.JdbcType;

import java.io.File;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * 代码生成器
 *
 * @author fanzezhen
 * @since 3.1.8
 */
public class Generator {
    private Generator() {
    }

    public static void fastAutoGenerator(Config config) {
        String pkType = "String";
        Class<?> superEntityClass = BaseGenericEntity.class;
        String[] tables = config.getTableNames();
        String[] tablePrefixes = config.getTablePrefixes();
        String moduleName = config.getModuleName();
        String parentPackageName = config.getParentPackage();
        String since = config.getSince();
        String author = System.getProperty("user.name");
        String logicDeleteColumnName = "DEL_FLAG";
        String moduleDir = config.getOutputDirOrCurrent() + (CharSequenceUtil.isNotBlank(moduleName) ?
            (moduleName.trim() + File.separator) : CharSequenceUtil.EMPTY) +
            "src" + File.separator + "main" + File.separator + "java" + File.separator;
        String packageDir = moduleDir + parentPackageName.replace(".", File.separator);
        FastAutoGenerator.create(config.getUrl(), config.getUsername(), config.getPassword())
            .dataSourceConfig(builder -> builder.typeConvertHandler((globalConfig, typeRegistry, metaInfo) -> {
                IColumnType columnType = typeRegistry.getColumnType(metaInfo);
                if (metaInfo.getColumnName().equals(logicDeleteColumnName) &&
                    DbColumnType.BYTE.equals(columnType) &&
                    JdbcType.TINYINT.equals(metaInfo.getJdbcType())) {
                    columnType = DbColumnType.INTEGER;
                } else if (DbColumnType.LOCAL_DATE_TIME.equals(columnType)) {
                    columnType = DbColumnType.DATE;
                }
                return columnType;
            }))
            .globalConfig(builder ->
                builder.author(author) // 设置作者
                    .disableOpenDir() //禁止打开输出目录
                    .commentDate(() -> since)
                    .outputDir(moduleDir) // 指定输出目录
            )
            .strategyConfig(builder -> {
                builder.addInclude(tables);
                if (tablePrefixes != null && tablePrefixes.length > 0) {
                    builder.addTablePrefix(tablePrefixes);
                }
            })
            .strategyConfig(builder -> builder.controllerBuilder().enableFileOverride().enableRestStyle())
            .strategyConfig(builder -> builder.serviceBuilder()
                .enableFileOverride()
                .superServiceClass(IService.class)
                .superServiceImplClass(ServiceImpl.class)
            )
            .strategyConfig(builder -> builder.mapperBuilder().enableFileOverride()
                .superClass(IBaseMapper.class)
                .disableMapperXml()
                .convertXmlFileName(entityName -> null)
            )
            .strategyConfig(builder -> builder.entityBuilder().enableFileOverride()
                .enableLombok()
                .enableChainModel()
                .logicDeleteColumnName(logicDeleteColumnName)
                .superClass(superEntityClass)
                .addSuperEntityColumns(Arrays.stream(ReflectUtil.getFields(BaseEntity.class)).map(field -> CharSequenceUtil.toUnderlineCase(field.getName())).collect(Collectors.toList()))
            )
            .packageConfig(builder -> builder
                .parent(parentPackageName) // 设置父包名
                .entity("model.entity") //设置entity包名
            )
            .injectionConfig(consumer -> consumer
                .customFile(file -> file
                    .enableFileOverride()
                    .fileName("BO.java")
                    .formatNameFunction(tableInfo -> CharSequenceUtil.upperFirst(CharSequenceUtil.toCamelCase(StringUtil.removeAnyPrefix(tableInfo.getName(), tablePrefixes))))
                    .templatePath("/templates/bo.java.vm")
                    .filePath(packageDir + "/model/bo"))
                .customFile(file -> file
                    .enableFileOverride()
                    .fileName("DTO.java")
                    .formatNameFunction(tableInfo -> CharSequenceUtil.upperFirst(CharSequenceUtil.toCamelCase(StringUtil.removeAnyPrefix(tableInfo.getName(), tablePrefixes))))
                    .templatePath("/templates/dto.java.vm")
                    .filePath(packageDir + "/model/dto"))
                .customFile(file -> file
                    .enableFileOverride()
                    .fileName("SearchDTO.java")
                    .formatNameFunction(tableInfo -> CharSequenceUtil.upperFirst(CharSequenceUtil.toCamelCase(StringUtil.removeAnyPrefix(tableInfo.getName(), tablePrefixes))))
                    .templatePath("/templates/searchDTO.java.vm")
                    .filePath(packageDir + "/model/dto"))
                .beforeOutputFile((tableInfo, objectMap) -> {
                    objectMap.put("name", CharSequenceUtil.upperFirst(CharSequenceUtil.toCamelCase(StringUtil.removeAnyPrefix(tableInfo.getName(), tablePrefixes))));
                    objectMap.put("path", StringUtil.removeAnyPrefix(tableInfo.getName(), tablePrefixes).replace(StrPool.UNDERLINE, StrPool.DASHED));
                })
                .customMap(new JSONObject()
                    .fluentPut("pkType", pkType)
                    .fluentPut("createTime", DateUtil.now())
                    .fluentPut("modelPackage", parentPackageName + ".model.")
                )
                .build())
            .execute();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Config {
        static final String DEFAULT_PARENT_PACKAGE = "com.github.fanzezhen.fun.framework.mp.generator";
        static final String DEFAULT_SINCE = "1.0.0";
        static final String DEFAULT_AUTHOR = System.getProperty("user.name");
        static final String[] DEFAULT_TABLE_PREFIXES = new String[]{"t_"};
        /**
         * 文件输出目录
         */
        private String outputDir;
        private String url;
        private String username;
        private String password;
        /**
         * 模块名
         */
        private String moduleName;
        private String parentPackage;
        private String since;
        private String author;
        private String[] tableNames;
        private String[] tablePrefixes;

        public static Config ofTable(String url, String username, String password, String... tables) {
            return new Config(url, username, password, DEFAULT_PARENT_PACKAGE, DEFAULT_SINCE, DEFAULT_AUTHOR, tables);
        }

        public static Config ofPackageTable(String url, String username, String password, String parentPackageName, String... tables) {
            return new Config(url, username, password, parentPackageName, DEFAULT_SINCE, DEFAULT_AUTHOR, tables);
        }

        public static Config ofSinceTable(String url, String username, String password, String since, String... tables) {
            return new Config(url, username, password, DEFAULT_PARENT_PACKAGE, since, DEFAULT_AUTHOR, tables);
        }

        public static Config ofPackageSinceTable(String url, String username, String password, String parentPackageName, String since, String... tables) {
            return new Config(url, username, password, parentPackageName, since, DEFAULT_AUTHOR, tables);
        }

        public Config(String url, String username, String password, String parentPackage, String since, String author, String... tableNames) {
            this.url = url;
            this.username = username;
            this.password = password;
            this.tableNames = tableNames;
            this.parentPackage = parentPackage;
            this.since = since;
            this.author = author;
        }

        public String getOutputDirOrCurrent() {
            String dir = CharSequenceUtil.isBlank(outputDir) ? System.getProperty("user.dir") : outputDir;
            if (!dir.endsWith(File.separator)) {
                dir += File.separator;
            }
            return dir;
        }

        public static void main(String[] args) {
            System.out.println(System.getProperty("user.dir"));
        }
    }
}
