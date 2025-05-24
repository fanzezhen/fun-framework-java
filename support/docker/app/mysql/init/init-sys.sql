CREATE DATABASE IF NOT EXISTS demo DEFAULT CHARSET utf8mb4 COLLATE utf8mb4_unicode_ci;
use demo;
drop table if exists sys_app;

drop table if exists sys_dict;

drop table if exists sys_dict_item;


/*==============================================================*/
/* Table: sys_app                                               */
/*==============================================================*/
create table sys_app
(
    id             varchar(50)  not null comment '主键',
    app_code       varchar(50)  not null comment '代码',
    app_name       varchar(255) not null comment '名称',
    status         tinyint      not null default 0 comment '状态（0--正常；1--停用）',
    version        int          not null default 0 comment '版本号',
    update_time    timestamp    not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '更新时间',
    update_user_id varchar(50) comment '最后更新人ID',
    create_time    timestamp    not null default CURRENT_TIMESTAMP comment '创建时间',
    create_user_id varchar(50) comment '创建人ID',
    del_flag       tinyint      not null default 0 comment '是否删除（0--否；1--是）',
    primary key (id)
);

alter table sys_app
    comment '系统应用表';

/*==============================================================*/
/* Index: ix_del_status                                         */
/*==============================================================*/
create index ix_del_status on sys_app
    (
     del_flag,
     status
        );

/*==============================================================*/
/* Table: sys_dict                                              */
/*==============================================================*/
create table sys_dict
(
    id             varchar(50) not null comment '主键',
    dict_code      varchar(20) not null comment '字典代码',
    dict_name      varchar(20) not null comment '代码名称',
    remark         varchar(50) comment '详细说明',
    order_num      smallint    not null default 1 comment '排序优先级',
    app_code       varchar(20) comment '所属应用',
    status         tinyint     not null default 0 comment '状态（0--正常；1--停用）',
    update_time    timestamp   not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '更新时间',
    update_user_id varchar(50) comment '最后更新人ID',
    create_time    timestamp   not null default CURRENT_TIMESTAMP comment '创建时间',
    create_user_id varchar(50) comment '创建人ID',
    del_flag       tinyint     not null default 0 comment '是否删除（0--否；1--是）',
    version        int         not null default 0 comment '版本号',
    primary key (id)
)
    comment '系统字典表';

alter table sys_dict
    comment '字典';

/*==============================================================*/
/* Index: ix_code                                               */
/*==============================================================*/
create index ix_code on sys_dict
    (
     dict_code
        );

/*==============================================================*/
/* Index: ix_status_del_app                                     */
/*==============================================================*/
create index ix_status_del_app on sys_dict
    (
     status,
     del_flag,
     app_code
        );

/*==============================================================*/
/* Table: sys_dict_item                                         */
/*==============================================================*/
create table sys_dict_item
(
    id             varchar(50) not null comment '主键',
    dict_id        varchar(50) not null comment '字典ID',
    item_code      varchar(20) not null comment '字典代码',
    item_name      varchar(20) not null comment '代码名称',
    remark         varchar(50) comment '详细说明',
    order_num      smallint    not null default 1 comment '排序优先级',
    app_code       varchar(20) comment '所属应用',
    status         tinyint     not null default 0 comment '状态（0--正常；1--停用）',
    create_user_id varchar(50) comment '创建人ID',
    create_time    timestamp   not null default CURRENT_TIMESTAMP comment '创建时间',
    update_time    timestamp   not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '更新时间',
    update_user_id varchar(50) comment '最后更新人ID',
    del_flag       tinyint     not null default 0 comment '是否删除（0--否；1--是）',
    version        int         not null default 0 comment '版本号',
    primary key (id)
)
    comment '系统字典表';

alter table sys_dict_item
    comment '字典项';

/*==============================================================*/
/* Index: ix_dict_code                                          */
/*==============================================================*/
create index ix_dict_code on sys_dict_item
    (
     dict_id,
     item_code
        );

/*==============================================================*/
/* Index: ix_status_del_app                                     */
/*==============================================================*/
create index ix_status_del_app on sys_dict_item
    (
     status,
     del_flag,
     app_code
        );
