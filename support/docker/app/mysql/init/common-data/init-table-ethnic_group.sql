-- drop index "idx_ethnic_group_name";

-- drop table "ethnic_group" cascade constraints;

/*==============================================================*/
/* Table: "ethnic_group"                                        */
/*==============================================================*/
create table "ethnic_group"
(
    "id"                 NUMBER               not null,
    "code"               VARCHAR(16),
    "name"               VARCHAR(32),
    "country_area_id"    NUMBER,
    constraint PK_ETHNIC_GROUP primary key ("id")
);

comment on table "ethnic_group" is
    '民族表';

comment on column "ethnic_group"."id" is
    '主键ID';

comment on column "ethnic_group"."code" is
    '代码';

comment on column "ethnic_group"."name" is
    '名称';

comment on column "ethnic_group"."country_area_id" is
    '所属国家ID';

/*==============================================================*/
/* Index: "idx_ethnic_group_name"                               */
/*==============================================================*/
create unique index "idx_ethnic_group_name" on "ethnic_group" (
                                                               "name" ASC
    );


-- 初始化area表数据
insert all
    into "ethnic_group"("id", "code", "name", "country_area_id") VALUES (1, '', '汉族', 86)
into "ethnic_group"("id", "code", "name", "country_area_id") VALUES (2, '', '蒙古族', 86)
into "ethnic_group"("id", "code", "name", "country_area_id") VALUES (3, '', '回族', 86)
into "ethnic_group"("id", "code", "name", "country_area_id") VALUES (4, '', '藏族', 86)
into "ethnic_group"("id", "code", "name", "country_area_id") VALUES (5, '', '维吾尔族', 86)
into "ethnic_group"("id", "code", "name", "country_area_id") VALUES (6, '', '苗族', 86)
into "ethnic_group"("id", "code", "name", "country_area_id") VALUES (7, '', '彝族', 86)
into "ethnic_group"("id", "code", "name", "country_area_id") VALUES (8, '', '壮族', 86)
into "ethnic_group"("id", "code", "name", "country_area_id") VALUES (9, '', '布依族', 86)
into "ethnic_group"("id", "code", "name", "country_area_id") VALUES (10, '', '朝鲜族', 86)
into "ethnic_group"("id", "code", "name", "country_area_id") VALUES (11, '', '满族', 86)
into "ethnic_group"("id", "code", "name", "country_area_id") VALUES (12, '', '侗族', 86)
into "ethnic_group"("id", "code", "name", "country_area_id") VALUES (13, '', '瑶族', 86)
into "ethnic_group"("id", "code", "name", "country_area_id") VALUES (14, '', '白族', 86)
into "ethnic_group"("id", "code", "name", "country_area_id") VALUES (15, '', '土家族', 86)
into "ethnic_group"("id", "code", "name", "country_area_id") VALUES (16, '', '哈尼族', 86)
into "ethnic_group"("id", "code", "name", "country_area_id") VALUES (17, '', '哈萨克族', 86)
into "ethnic_group"("id", "code", "name", "country_area_id") VALUES (18, '', '傣族', 86)
into "ethnic_group"("id", "code", "name", "country_area_id") VALUES (19, '', '黎族', 86)
into "ethnic_group"("id", "code", "name", "country_area_id") VALUES (20, '', '傈僳族', 86)
into "ethnic_group"("id", "code", "name", "country_area_id") VALUES (21, '', '佤族', 86)
into "ethnic_group"("id", "code", "name", "country_area_id") VALUES (22, '', '畲族', 86)
into "ethnic_group"("id", "code", "name", "country_area_id") VALUES (23, '', '高山族', 86)
into "ethnic_group"("id", "code", "name", "country_area_id") VALUES (24, '', '拉祜族', 86)
into "ethnic_group"("id", "code", "name", "country_area_id") VALUES (25, '', '水族', 86)
into "ethnic_group"("id", "code", "name", "country_area_id") VALUES (26, '', '东乡族', 86)
into "ethnic_group"("id", "code", "name", "country_area_id") VALUES (27, '', '纳西族', 86)
into "ethnic_group"("id", "code", "name", "country_area_id") VALUES (28, '', '景颇族', 86)
into "ethnic_group"("id", "code", "name", "country_area_id") VALUES (29, '', '柯尔克孜族', 86)
into "ethnic_group"("id", "code", "name", "country_area_id") VALUES (30, '', '土族', 86)
into "ethnic_group"("id", "code", "name", "country_area_id") VALUES (31, '', '达斡尔族', 86)
into "ethnic_group"("id", "code", "name", "country_area_id") VALUES (32, '', '仫佬族', 86)
into "ethnic_group"("id", "code", "name", "country_area_id") VALUES (33, '', '羌族', 86)
into "ethnic_group"("id", "code", "name", "country_area_id") VALUES (34, '', ' 布朗族', 86)
into "ethnic_group"("id", "code", "name", "country_area_id") VALUES (35, '', ' 撒拉族', 86)
into "ethnic_group"("id", "code", "name", "country_area_id") VALUES (36, '', ' 毛难族', 86)
into "ethnic_group"("id", "code", "name", "country_area_id") VALUES (37, '', ' 仡佬族', 86)
into "ethnic_group"("id", "code", "name", "country_area_id") VALUES (38, '', ' 锡伯族', 86)
into "ethnic_group"("id", "code", "name", "country_area_id") VALUES (39, '', ' 阿昌族', 86)
into "ethnic_group"("id", "code", "name", "country_area_id") VALUES (40, '', ' 普米族', 86)
into "ethnic_group"("id", "code", "name", "country_area_id") VALUES (41, '', ' 塔吉克族', 86)
into "ethnic_group"("id", "code", "name", "country_area_id") VALUES (42, '', ' 怒族', 86)
into "ethnic_group"("id", "code", "name", "country_area_id") VALUES (43, '', ' 乌孜别克族', 86)
into "ethnic_group"("id", "code", "name", "country_area_id") VALUES (44, '', ' 俄罗斯族', 86)
into "ethnic_group"("id", "code", "name", "country_area_id") VALUES (45, '', ' 鄂温克族', 86)
into "ethnic_group"("id", "code", "name", "country_area_id") VALUES (46, '', ' 崩龙族', 86)
into "ethnic_group"("id", "code", "name", "country_area_id") VALUES (47, '', ' 保安族', 86)
into "ethnic_group"("id", "code", "name", "country_area_id") VALUES (48, '', ' 裕固族', 86)
into "ethnic_group"("id", "code", "name", "country_area_id") VALUES (49, '', ' 京族', 86)
into "ethnic_group"("id", "code", "name", "country_area_id") VALUES (50, '', ' 塔塔尔族', 86)
into "ethnic_group"("id", "code", "name", "country_area_id") VALUES (51, '', ' 独龙族', 86)
into "ethnic_group"("id", "code", "name", "country_area_id") VALUES (52, '', ' 鄂伦春族', 86)
into "ethnic_group"("id", "code", "name", "country_area_id") VALUES (53, '', ' 赫哲族', 86)
into "ethnic_group"("id", "code", "name", "country_area_id") VALUES (54, '', ' 门巴族', 86)
into "ethnic_group"("id", "code", "name", "country_area_id") VALUES (55, '', ' 珞巴族', 86)
into "ethnic_group"("id", "code", "name", "country_area_id") VALUES (56, '', ' 基诺族', 86)
into "ethnic_group"("id", "code", "name", "country_area_id") VALUES (57, '', ' 其他', 86)
select 1
from dual;