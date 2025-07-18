drop table if exists sys_app;
create table sys_app
(
    id             integer      not null auto_increment comment '主键',
    create_user_id integer comment '创建人ID',
    create_time    timestamp    not null default CURRENT_TIMESTAMP comment '创建时间',
    update_user_id integer comment '最后更新人ID',
    update_time    timestamp    not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '更新时间',
    del_flag       integer      not null default 0 comment '是否删除（0--否；非0即为删除）',
    version        integer      not null default 0 comment '版本号',
    code           varchar(50)  not null comment '代码',
    name           varchar(255) not null comment '名称',
    status         tinyint      not null default 0 comment '状态（0--正常；1--停用）',
    primary key (id)
) comment '系统-应用表';


drop table if exists sys_dict;
create table sys_dict
(
    id             integer     not null auto_increment comment '主键',
    create_user_id integer comment '创建人ID',
    create_time    timestamp   not null default CURRENT_TIMESTAMP comment '创建时间',
    update_user_id integer comment '最后更新人ID',
    update_time    timestamp   not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '更新时间',
    del_flag       integer     not null default 0 comment '是否删除（0--否；非0即为删除）',
    version        integer     not null default 0 comment '版本号',
    app_id         smallint comment '所属应用',
    name           varchar(50) not null comment '字典名称',
    remark         text comment '详细说明',
    order_num      smallint    not null default 1 comment '排序优先级',
    disabled       tinyint     not null default 0 comment '是否禁用（0--正常；1--停用）',
    primary key (id),
    unique key uk_del_name_app (del_flag, name, app_id)
)
    comment '系统-字典表';


drop table if exists sys_dict_item;
create table sys_dict_item
(
    id             integer     not null auto_increment comment '主键',
    create_user_id integer comment '创建人ID',
    create_time    timestamp   not null default CURRENT_TIMESTAMP comment '创建时间',
    update_user_id integer comment '最后更新人ID',
    update_time    timestamp   not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '更新时间',
    del_flag       integer     not null default 0 comment '是否删除（0--否；非0即为删除）',
    version        integer     not null default 0 comment '版本号',
    dict_id        integer     not null comment '字典ID',
    name           varchar(50) not null comment '名称',
    remark         text comment '详细说明',
    order_num      smallint    not null default 1 comment '排序优先级',
    disabled       tinyint     not null default 0 comment '是否禁用（0--正常；1--停用）',
    primary key (id),
    unique key uk_dict_del_name (dict_id, del_flag, name)
)
    comment '系统-字典项表';
