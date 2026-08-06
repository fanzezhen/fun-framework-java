-- 多租户隔离端到端测试用建表脚本
-- biz_order 含租户列，应被自动隔离；sys_dict 无租户列，应放行
DROP TABLE IF EXISTS biz_order;
CREATE TABLE biz_order
(
    id        BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT,
    order_no  VARCHAR(64)
);

DROP TABLE IF EXISTS sys_dict;
CREATE TABLE sys_dict
(
    id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    dict_key VARCHAR(64)
);

INSERT INTO sys_dict (dict_key)
VALUES ('k1'),
       ('k2');
