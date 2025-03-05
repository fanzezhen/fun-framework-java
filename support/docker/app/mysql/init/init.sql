# use mysql;
# update user set host='%' where account_locked='N';
# Grant all privileges on *.* to 'root'@'%';
# FLUSH PRIVILEGES;
CREATE DATABASE IF NOT EXISTS nacos DEFAULT CHARSET utf8mb4 COLLATE utf8mb4_general_ci;
use nacos;
CREATE DATABASE IF NOT EXISTS dev DEFAULT CHARSET utf8mb4 COLLATE utf8mb4_general_ci;
use dev;