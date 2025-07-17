package com.github.fanzezhen.fun.framework.mp.generator;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.text.StrPool;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.github.fanzezhen.fun.framework.mp.base.entity.increment.BaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;
import java.util.Scanner;

/**
 * @author fanzezhen
 */
@Slf4j
@Accessors(chain = true)
@Data
public class GeneratorBean {
    /**
     * 作者
     */
    private String author;
    /**
     * db链接地址
     */
    private String dbUrl;
    /**
     * db用户登录名称
     */
    private String dbUsername;
    /**
     * db用户登录密码
     */
    private String dbPassword;
    /**
     * 模块名（fun-framework-mp）
     */
    private String moduleName;
    /**
     * 父包名（"com.github.fanzezhen.fun.framework.mp.generator"）
     */
    private String parentPackageName;
    private String javaDir;
    private String mapperDir;
    /**
     * 输入表名的分隔符
     */
    private String tableNameSplitter;
    /**
     * 输入的表名字符串
     */
    private String tableNameStr;
    /**
     * 数据表模型类的父类名
     */
    private Class<?> superEntityClass;
    /**
     * 数据表中的逻辑删除字段
     */
    private String logicDeleteColumnName;
    /**
     * 数据表中的版本号字段
     */
    private String versionFieldName;
    private String formatFileName;
    private String[] ignoreTablePrefix;
    private Scanner scanner;

    public GeneratorBean() {
        setJavaDir(MpConstant.DEFAULT_DIR_JAVA);
        setMapperDir(MpConstant.DEFAULT_DIR_MAPPER);
        setLogicDeleteColumnName(MpConstant.DEFAULT_LOGIC_DELETE_COLUMN_NAME);
        setVersionFieldName(MpConstant.VERSION_FIELD_NAME);
        setFormatFileName(MpConstant.DEFAULT_FORMAT_FILE_NAME);
        setIgnoreTablePrefix(MpConstant.DEFAULT_IGNORE_TABLE_PREFIX);
        setTableNameSplitter(StrPool.COMMA);
        setSuperEntityClass(BaseEntity.class);
    }

    public GeneratorBean(String dbUrl, String dbUsername, String dbPassword) {
        this();
        this.dbUrl = dbUrl;
        this.dbUsername = dbUsername;
        this.dbPassword = dbPassword;
    }

    /**
     * <p>
     * 读取控制台内容
     * </p>
     */
    public String scanner(String tip) {
        log.info("请输入{}：", tip);
        String nextLine = getOrSystemScanner().nextLine();
        return StringUtils.isBlank(nextLine) ? getOrSystemScanner().next() : nextLine;
    }

    public Scanner getOrSystemScanner() {
        if (scanner == null) {
            scanner = new Scanner(System.in);
        }
        return scanner;
    }

    public List<String> getTableNameList() {
        return CharSequenceUtil.isBlank(tableNameStr) ? Collections.emptyList() : CharSequenceUtil.split(tableNameStr, getTableNameSplitter(), true, true);
    }
}
