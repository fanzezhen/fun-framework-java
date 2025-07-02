drop table if exists sys_permission;

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
);

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

drop table if exists sys_role;

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

drop table if exists sys_role_permission;

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

drop table if exists auth_user;
create table auth_user
(
  id             varchar(50)      not null ,
  oid            varchar(50) null comment '三方id',
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
  del_flag       bigint     not null default 0 comment '是否删除，非0即为删除',
  primary key (id),
  unique key uk_username_del (username, del_flag),
  index ix_oid_del (oid, del_flag)
)
  comment '系统用户表' DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;


drop table if exists sys_user_role;
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
