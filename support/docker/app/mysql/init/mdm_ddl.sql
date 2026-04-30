DROP TABLE IF EXISTS mdm_form;
CREATE TABLE mdm_form
(
    id             BIGINT            NOT NULL AUTO_INCREMENT COMMENT '主键',
    create_user_id BIGINT COMMENT '创建人ID',
    create_time    TIMESTAMP         NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_user_id BIGINT COMMENT '最后更新人ID',
    update_time    TIMESTAMP         NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    del_flag       BIGINT            NOT NULL DEFAULT 0 COMMENT '是否删除（0--否；非0即为删除）',
    version        SMALLINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '版本号',
    tenant_id      BIGINT COMMENT '租户ID',
    name           VARCHAR(50)       NOT NULL COMMENT '名称',
    remark         TEXT COMMENT '详细说明',
    released       BOOLEAN           NOT NULL DEFAULT FALSE COMMENT '是否已发布',
    order_num      SMALLINT          NOT NULL DEFAULT 1 COMMENT '排序优先级',
    PRIMARY KEY (ID),
    UNIQUE KEY UK_DEL_TENANT_NAME (del_flag, tenant_id, name)
)
    COMMENT '动态表单';

DROP TABLE IF EXISTS mdm_form_def;
CREATE TABLE mdm_form_def
(
    id             BIGINT            NOT NULL AUTO_INCREMENT COMMENT '主键',
    create_user_id BIGINT COMMENT '创建人ID',
    create_time    TIMESTAMP         NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_user_id BIGINT COMMENT '最后更新人ID',
    update_time    TIMESTAMP         NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    del_flag       BIGINT            NOT NULL DEFAULT 0 COMMENT '是否删除（0--否；非0即为删除）',
    version        SMALLINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '版本号',
    tenant_id      BIGINT COMMENT '租户ID',
    form_id        BIGINT            NOT NULL COMMENT '表单ID',
    version_code   VARCHAR(50)       NOT NULL COMMENT '版本标识',
    data           JSON COMMENT '数据',
    valid          BOOLEAN           NOT NULL DEFAULT FALSE COMMENT '是否生效中',
    PRIMARY KEY (ID),
    UNIQUE KEY UK_DEL_TENANT_FORM_VERSION (del_flag, tenant_id, form_id, version_code)
)
    COMMENT '动态表单定义';

DROP TABLE IF EXISTS mdm_form_data;
CREATE TABLE mdm_form_data
(
    id             BIGINT            NOT NULL AUTO_INCREMENT COMMENT '主键',
    create_user_id BIGINT COMMENT '创建人ID',
    create_time    TIMESTAMP         NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_user_id BIGINT COMMENT '最后更新人ID',
    update_time    TIMESTAMP         NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    del_flag       BIGINT            NOT NULL DEFAULT 0 COMMENT '是否删除（0--否；非0即为删除）',
    version        SMALLINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '版本号',
    tenant_id      BIGINT COMMENT '租户ID',
    form_id        BIGINT            NOT NULL COMMENT '表单ID',
    form_def_id    BIGINT            NOT NULL COMMENT '表单定义ID',
    PRIMARY KEY (ID),
    INDEX IX_FORM (form_id),
    INDEX IX_FORM_DEF (form_def_id)
)
    COMMENT '动态表单数据';

DROP TABLE IF EXISTS mdm_form_item_data;
CREATE TABLE mdm_form_item_data
(
    id             BIGINT            NOT NULL AUTO_INCREMENT COMMENT '主键',
    create_user_id BIGINT COMMENT '创建人ID',
    create_time    TIMESTAMP         NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_user_id BIGINT COMMENT '最后更新人ID',
    update_time    TIMESTAMP         NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    del_flag       BIGINT            NOT NULL DEFAULT 0 COMMENT '是否删除（0--否；非0即为删除）',
    version        SMALLINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '版本号',
    tenant_id      BIGINT COMMENT '租户ID',
    form_data_id   BIGINT            NOT NULL COMMENT '表单数据ID',
    form_item_code VARCHAR(50)       NOT NULL COMMENT '表单字段标识',
    seq            SMALLINT          NOT NULL DEFAULT -1 COMMENT '字段序号(单值为-1,列表从0开始)',
    value          TEXT COMMENT '值',
    value_type     VARCHAR(20) COMMENT '类型',
    format         VARCHAR(20) COMMENT '格式',
    PRIMARY KEY (ID),
    UNIQUE KEY UK_DEL_TENANT_FORM_ITEM_SEQ (del_flag, tenant_id, form_data_id, form_item_code, seq)
)
    COMMENT '动态表单字段数据';
