drop table if exists auth_element;
create table auth_element
(
    id             integer     not null auto_increment comment 'ID',
    create_user_id integer comment '创建人ID',
    create_time    timestamp   not null default CURRENT_TIMESTAMP comment '创建时间',
    update_user_id integer comment '最后更新人ID',
    update_time    timestamp   not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '更新时间',
    del_flag       integer     not null default 0 comment '是否删除（0--否；非0即为删除）',
    version        integer     not null default 0 comment '版本号',
    pid            integer     not null comment '上级ID',
    code           varchar(50) not null comment '标识',
    name           varchar(255) comment '名称',
    uri            varchar(255) comment '地址',
    type           tinyint     not null default 1 comment '元素类型（1--菜单；2--按钮；3--虚拟）',
    order_num      smallint             default 1 comment '排序优先级',
    disabled       tinyint     not null default 0 comment '是否禁用（0--正常；1--停用）',
    app_id       smallint comment '所属应用',
    primary key (id),
    index ix_del_app (del_flag, app_id)
) comment '鉴权-页面元素表';

drop table if exists auth_element_api;
create table auth_element_api
(
    id             integer      not null auto_increment comment 'ID',
    create_user_id integer comment '创建人ID',
    create_time    timestamp    not null default CURRENT_TIMESTAMP comment '创建时间',
    update_user_id integer comment '最后更新人ID',
    update_time    timestamp    not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '更新时间',
    del_flag       integer      not null default 0 comment '是否删除（0--否；非0即为删除）',
    version        integer      not null default 0 comment '版本号',
    element_id     integer      not null comment '页面元素ID',
    uri            varchar(255) not null comment '接口地址',
    name           varchar(255) comment '名称',
    disabled       tinyint      not null default 0 comment '是否禁用（0--正常；1--停用）',
    primary key (id),
    index ix_del_element (del_flag, element_id)
) comment '鉴权-页面元素接口表';


drop table if exists auth_role;
create table auth_role
(
    id             integer     not null auto_increment comment '主键',
    create_user_id integer comment '创建人ID',
    create_time    timestamp   not null default CURRENT_TIMESTAMP comment '创建时间',
    update_user_id integer comment '最后更新人ID',
    update_time    timestamp   not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '更新时间',
    del_flag       integer     not null default 0 comment '是否删除（0--否；非0即为删除）',
    version        integer     not null default 0 comment '版本号',
    app_id       smallint comment '所属应用',
    role_name      varchar(50) not null comment '角色名',
    admin          tinyint     not null default 0 comment '是否管理员',
    description    text comment '释义',
    disabled       tinyint     not null default 0 comment '是否禁用（0--正常；1--停用）',
    primary key (id),
    unique key uk_name_del_app (role_name, del_flag, app_id)
) comment '鉴权-角色表';


drop table if exists auth_role_element;
create table auth_role_element
(
    id             integer   not null auto_increment comment '主键',
    create_user_id integer comment '创建人ID',
    create_time    timestamp not null default CURRENT_TIMESTAMP comment '创建时间',
    update_user_id integer comment '最后更新人ID',
    update_time    timestamp not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '更新时间',
    del_flag       integer   not null default 0 comment '是否删除（0--否；非0即为删除）',
    version        integer   not null default 0 comment '版本号',
    role_id        integer   not null comment '角色ID',
    element_id     integer   not null comment '权限ID',
    primary key (id),
    unique key uk_del_role_element (del_flag, role_id, element_id)
) comment '鉴权-角色页面元素关联表';


drop table if exists auth_user;
create table auth_user
(
    id             integer     not null auto_increment comment '主键',
    create_user_id integer comment '创建人ID',
    create_time    timestamp   not null default CURRENT_TIMESTAMP comment '创建时间',
    update_user_id integer comment '最后更新人ID',
    update_time    timestamp   not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '更新时间',
    del_flag       integer     not null default 0 comment '是否删除（0--否；非0即为删除）',
    version        integer     not null default 0 comment '版本号',
    oid            varchar(50) null comment '三方id',
    username       varchar(20) not null comment '用户名',
    password       varchar(50) not null comment '密码',
    nickname       varchar(50) not null default '昵称' comment '昵称',
    avatar         varchar(255) comment '头像地址',
    email          varchar(255) comment '邮箱',
    phone          varchar(16) comment '联系电话',
    remark         varchar(255) comment '备注',
    last_time      timestamp   not null default CURRENT_TIMESTAMP comment '最后操作时间',
    status         tinyint     not null default 0 comment '状态（0--正常；1--停用）',
    primary key (id),
    unique key uk_del_username (del_flag, username)
) comment '鉴权-用户表';


drop table if exists auth_user_role;
create table auth_user_role
(
    id             integer   not null auto_increment comment '主键',
    create_user_id integer comment '创建人ID',
    create_time    timestamp not null default CURRENT_TIMESTAMP comment '创建时间',
    update_user_id integer comment '最后更新人ID',
    update_time    timestamp not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '更新时间',
    del_flag       integer   not null default 0 comment '是否删除（0--否；非0即为删除）',
    version        integer   not null default 0 comment '版本号',
    user_id        integer   not null comment '用户ID',
    role_id        integer   not null comment '角色ID',
    primary key (id),
    unique key uk_del_user_role (del_flag, user_id, role_id)
) comment '鉴权-用户角色关联表';
