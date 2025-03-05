CREATE DATABASE IF NOT EXISTS demo DEFAULT CHARSET utf8mb4 COLLATE utf8mb4_general_ci;
use demo;
drop table if exists log_exception;

drop table if exists log_login;

drop table if exists log_operation;

drop table if exists log_operation_detail;

/*==============================================================*/
/* Table: log_exception                                         */
/*==============================================================*/
create table log_exception
(
    id                 varchar(50) not null,
    log_type           tinyint comment '日志类型',
    modular            varchar(20) comment '资源所属模块',
    class_name         varchar(20) comment '错误类名',
    message            varchar(20) comment '错误说明',
    stack_trace        text comment '错误堆栈',
    operation_username varchar(50) comment '操作人名称',
    app_code           varchar(5) comment '应用代码',
    update_time        timestamp   not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '更新时间',
    update_user_id     varchar(50) comment '最后更新人ID',
    create_user_id     varchar(50) not null comment '创建人ID',
    create_time        timestamp            default CURRENT_TIMESTAMP comment '创建时间',
    del_flag           tinyint     not null default 0 comment '是否删除（0--否；1--是）',
    primary key (id)
)
    comment '异常日志表';

/*==============================================================*/
/* Index: ix_del_app_time                                       */
/*==============================================================*/
create index ix_del_app_time on log_exception
    (
     del_flag,
     app_code,
     update_time
        );

/*==============================================================*/
/* Table: log_login                                             */
/*==============================================================*/
create table log_login
(
    id              varchar(50) not null,
    username        varchar(15) not null comment '用户名',
    log_type        tinyint comment '1--登录成功； 2--登录失败； 3--退出登录',
    ip              varbinary(16) comment 'ip',
    os              varchar(20) comment '操作系统',
    browser_name    varchar(15) comment '浏览器名称',
    browser_version varchar(15) comment '浏览器版本',
    remark          varchar(15) comment '备注',
    update_time     timestamp   not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '更新时间',
    update_user_id  varchar(50) comment '最后更新人ID',
    create_user_id  varchar(50) comment '创建人ID',
    create_time     timestamp   not null default CURRENT_TIMESTAMP comment '创建时间',
    del_flag        tinyint     not null default 0 comment '是否删除（0--否；1--是）',
    primary key (id)
)
    comment '登录日志表';

/*==============================================================*/
/* Index: ix_username_type_time                                 */
/*==============================================================*/
create index ix_username_type_time on log_login
    (
     username,
     log_type,
     del_flag,
     update_time
        );

/*==============================================================*/
/* Table: log_operation                                         */
/*==============================================================*/
create table log_operation
(
    id                 varchar(50)  not null comment '主键',
    biz_id             varchar(50)  not null comment '业务Id',
    table_name         varchar(100) not null comment '表名称',
    operation_type     tinyint      not null comment '操作类型',
    module             varchar(50) comment '操作模块',
    app_code           varchar(50) comment 'APP标识',
    ip_address         varchar(50) comment 'IP地址',
    device_num         varchar(100) comment '设备号',
    remark             varchar(255) comment '备注',
    operation_username varchar(50) comment '操作人名称',
    update_time        timestamp    not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '更新时间',
    update_user_id     varchar(50) comment '最后更新人ID',
    create_user_id     varchar(50) comment '创建者Id',
    create_time        timestamp             default CURRENT_TIMESTAMP comment '创建时间',
    del_flag           tinyint      not null default 0 comment '是否删除（0--否；1--是）',
    primary key (id)
);

alter table log_operation
    comment '操作日志表';

/*==============================================================*/
/* Index: ix_create_time                                        */
/*==============================================================*/
create index ix_create_time on log_operation
    (
     create_time
        );

/*==============================================================*/
/* Index: ix_module_operate_type_create_user_id                 */
/*==============================================================*/
create index ix_module_operate_type_create_user_id on log_operation
    (
     module,
     operation_type,
     create_user_id
        );

/*==============================================================*/
/* Table: log_operation_detail                                  */
/*==============================================================*/
create table log_operation_detail
(
    id             varchar(50)  not null comment '主键',
    log_id         varchar(50)  not null comment '操作日志Id',
    table_column   varchar(100) not null comment '列字段',
    column_name    varchar(100) comment '列名称',
    old_value      varchar(255) comment 'oldValue',
    new_value      varchar(255) comment 'newValue',
    remark         varchar(255) comment '备注',
    app_code       varchar(50) comment 'APP标识',
    update_time    timestamp    not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '更新时间',
    update_user_id varchar(50) comment '最后更新人ID',
    create_user_id varchar(50) comment '创建者Id',
    create_time    timestamp             default CURRENT_TIMESTAMP comment '生成时间',
    del_flag       tinyint      not null default 0 comment '是否删除（0--否；1--是）',
    primary key (id)
);

alter table log_operation_detail
    comment '操作日志详情表';

/*==============================================================*/
/* Index: ix_log_id                                             */
/*==============================================================*/
create index ix_log_id on log_operation_detail
    (
     log_id
        );
