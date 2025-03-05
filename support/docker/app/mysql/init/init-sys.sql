CREATE DATABASE IF NOT EXISTS demo DEFAULT CHARSET utf8mb4 COLLATE utf8mb4_general_ci;
use demo;
drop table if exists sys_app;

drop table if exists sys_dict;

drop table if exists sys_dict_item;

drop table if exists sys_permission;

drop table if exists sys_role;

drop table if exists sys_role_permission;

drop table if exists sys_user;

drop table if exists sys_user_role;

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

/*==============================================================*/
/* Table: sys_permission                                        */
/*==============================================================*/
create table sys_permission
(
    id              varchar(50)  not null comment 'ID',
    pid             varchar(50)  not null comment '上级ID',
    display_name    varchar(255) not null comment '显示名称',
    operation_url   varchar(255) comment '请求地址',
    permission_type tinyint               default 1 comment '是否为菜单（1--菜单；2--按钮）',
    order_num       smallint              default 1 comment '排序优先级',
    status          tinyint      not null default 0 comment '状态（0--正常；1--停用）',
    update_time     timestamp    not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '更新时间',
    update_user_id  varchar(50) comment '最后更新人ID',
    create_time     timestamp    not null default CURRENT_TIMESTAMP comment '创建时间',
    create_user_id  varchar(50) comment '创建人ID',
    del_flag        tinyint      not null default 0 comment '是否删除（0--否；1--是）',
    version         int          not null default 0 comment '版本号',
    app_code        varchar(20) comment '所属应用',
    primary key (id)
)
    auto_increment = 10000;

alter table sys_permission
    comment '菜单、按钮表';

/*==============================================================*/
/* Index: ix_status_del_app                                     */
/*==============================================================*/
create index ix_status_del_app on sys_permission
    (
     status,
     del_flag,
     app_code
        );

/*==============================================================*/
/* Table: sys_role                                              */
/*==============================================================*/
create table sys_role
(
    id             bigint      not null auto_increment,
    role_name      varchar(64) not null comment '角色名',
    role_type      tinyint     not null comment '角色类型',
    description    varchar(255) comment '释义',
    app_code       varchar(20) comment '所属应用',
    status         tinyint     not null default 0 comment '状态（0--正常；1--停用）',
    version        int         not null default 0 comment '版本号',
    update_time    timestamp   not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '更新时间',
    update_user_id varchar(50) comment '最后更新人ID',
    create_time    timestamp   not null default CURRENT_TIMESTAMP comment '创建时间',
    create_user_id varchar(50) comment '创建人ID',
    del_flag       tinyint     not null default 0 comment '是否删除（0--否；1--是）',
    primary key (id),
    unique key uk_role_name_type (role_name)
)
    auto_increment = 10000;

alter table sys_role
    comment '系统角色表';

/*==============================================================*/
/* Index: ix_status_del_app                                     */
/*==============================================================*/
create index ix_status_del_app on sys_role
    (
     status,
     del_flag,
     app_code
        );

/*==============================================================*/
/* Table: sys_role_permission                                   */
/*==============================================================*/
create table sys_role_permission
(
    id             bigint    not null auto_increment comment '主键',
    role_id        bigint    not null comment '角色ID',
    permission_id  bigint    not null comment '权限ID',
    create_time    timestamp not null default CURRENT_TIMESTAMP comment '创建时间',
    create_user_id varchar(50) comment '创建人ID',
    update_time    timestamp not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '更新时间',
    update_user_id varchar(50) comment '最后更新人ID',
    del_flag       tinyint   not null default 0 comment '是否删除（0--否；1--是）',
    primary key (id),
    unique key uk_role_permission_role_id_permission_id (role_id, permission_id)
)
    auto_increment = 10000;

alter table sys_role_permission
    comment '角色权限关联表';

/*==============================================================*/
/* Index: ix_role_pms_del                                       */
/*==============================================================*/
create index ix_role_pms_del on sys_role_permission
    (
     role_id,
     permission_id,
     del_flag
        );

/*==============================================================*/
/* Table: sys_user                                              */
/*==============================================================*/
create table sys_user
(
    id             bigint      not null auto_increment,
    username       varchar(16) not null comment '用户名',
    password       varchar(64) not null comment '密码',
    nickname       varchar(64) not null default '昵称' comment '昵称',
    avatar         varchar(255) comment '头像地址',
    email          varchar(255) comment '邮箱',
    phone          varchar(16) comment '联系电话',
    remark         varchar(255) comment '备注',
    last_time      timestamp   not null default CURRENT_TIMESTAMP comment '最后操作时间',
    status         tinyint     not null default 0 comment '状态（0--正常；1--停用）',
    version        int         not null default 0 comment '版本号',
    update_time    timestamp   not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '更新时间',
    update_user_id varchar(50) comment '最后更新人ID',
    create_time    timestamp   not null default CURRENT_TIMESTAMP comment '创建时间',
    create_user_id varchar(50) comment '创建人ID',
    del_flag       tinyint     not null default 0 comment '是否删除（0--否；1--是）',
    primary key (id),
    unique key uk_username_df (username)
)
    auto_increment = 10000;

alter table sys_user
    comment '系统用户表';

/*==============================================================*/
/* Index: ix_status_del_app                                     */
/*==============================================================*/
create index ix_status_del_app on sys_user
    (
     status,
     del_flag
        );

/*==============================================================*/
/* Index: ix_username                                           */
/*==============================================================*/
create index ix_username on sys_user
    (
     username
        );

/*==============================================================*/
/* Table: sys_user_role                                         */
/*==============================================================*/
create table sys_user_role
(
    id             bigint    not null auto_increment comment '主键',
    user_id        bigint    not null comment '用户ID',
    role_id        bigint    not null comment '角色ID',
    update_time    timestamp not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '更新时间',
    update_user_id varchar(50) comment '最后更新人ID',
    create_time    timestamp not null default CURRENT_TIMESTAMP comment '创建时间',
    create_user_id varchar(50) comment '创建人ID',
    del_flag       tinyint   not null default 0 comment '是否删除（0--否；1--是）',
    primary key (id),
    unique key uk_user_role_user_id_role_id (user_id, role_id)
)
    auto_increment = 10000;

alter table sys_user_role
    comment '系统用户角色表';

/*==============================================================*/
/* Index: ix_user_role_del_app                                  */
/*==============================================================*/
create index ix_user_role_del_app on sys_user_role
    (
     user_id,
     role_id,
     del_flag
        );
