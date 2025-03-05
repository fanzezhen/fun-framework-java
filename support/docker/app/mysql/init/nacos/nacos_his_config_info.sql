INSERT INTO nacos.his_config_info (id, nid, data_id, group_id, app_name, content, md5, gmt_create, gmt_modified, src_user, src_ip, op_type, tenant_id, encrypted_data_key) VALUES (0, 1, 'demo-sys-public', 'DEMO', '', 'server.port=10002
server.address = 0.0.0.0
debug=true
# root 日志级别以WARN级别输出
logging.level.root=INFO
# springframework.web日志以DEBUG级别输出
logging.level.org.springframework.web=DEBUG
# 文件位置
logging.file.name=log/${spring.application.name}.log
logging.file.max-size=1MB
# 数据库配置基础信息
spring.datasource.url=jdbc:mysql://localhost:3306/demo?serverTimezone=UTC&characterEncoding=utf-8&useUnicode=true&useSSL=false
spring.datasource.username=root
spring.datasource.password=root
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
# 连接池中最大的活跃连接数
spring.datasource.tomcat.max-active=10
# MyBatis
# 全局地开启或关闭配置文件中的所有映射器已经配置的任何缓存，默认为 true
mybatis-plus.configuration.cache-enabled=false
# 这个配置会将执行的sql打印出来，在开发或测试的时候可以用
mybatis-plus.configuration.log-impl=org.apache.ibatis.logging.stdout.StdOutImpl
# CAS服务地址
security.cas.server.host.url=http://localhost:8080/cas
# CAS服务登录地址
security.cas.server.host.login_url=${security.cas.server.host.url}/login
# CAS服务登出地址
security.cas.server.host.logout_url=${security.cas.server.host.url}/logout?service=${security.app.host.url}
# security获取用户信息接口地址
security.remote.user.detail.url=http://localhost:10002/public
# 应用访问地址
security.app.url=http://localhost:${server.port}
# 不校验权限的接口
security.ignoring.ant.matchers=/test, /tmp
#应用访问地址
security.app.host.url=http://localhost:${server.port}
#应用登录地址
security.app.login.url=/login
#应用登出地址
security.app.logout.url=/logout
', '284362a1a6e61b750f1855bc9613ef4e', '2010-05-05 00:00:00', '2022-09-19 11:12:52', null, '172.19.0.1', 'I', 'dev', '');
INSERT INTO nacos.his_config_info (id, nid, data_id, group_id, app_name, content, md5, gmt_create, gmt_modified, src_user, src_ip, op_type, tenant_id, encrypted_data_key) VALUES (1, 3, 'demo-sys-public', 'DEMO', '', 'server.port=10002
server.address = 0.0.0.0
debug=true
# root 日志级别以WARN级别输出
logging.level.root=INFO
# springframework.web日志以DEBUG级别输出
logging.level.org.springframework.web=DEBUG
# 文件位置
logging.file.name=log/${spring.application.name}.log
logging.file.max-size=1MB
# 数据库配置基础信息
spring.datasource.url=jdbc:mysql://localhost:3306/demo?serverTimezone=UTC&characterEncoding=utf-8&useUnicode=true&useSSL=false
spring.datasource.username=root
spring.datasource.password=root
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
# 连接池中最大的活跃连接数
spring.datasource.tomcat.max-active=10
# MyBatis
# 全局地开启或关闭配置文件中的所有映射器已经配置的任何缓存，默认为 true
mybatis-plus.configuration.cache-enabled=false
# 这个配置会将执行的sql打印出来，在开发或测试的时候可以用
mybatis-plus.configuration.log-impl=org.apache.ibatis.logging.stdout.StdOutImpl
# CAS服务地址
security.cas.server.host.url=http://localhost:8080/cas
# CAS服务登录地址
security.cas.server.host.login_url=${security.cas.server.host.url}/login
# CAS服务登出地址
security.cas.server.host.logout_url=${security.cas.server.host.url}/logout?service=${security.app.host.url}
# security获取用户信息接口地址
security.remote.user.detail.url=http://localhost:10002/public
# 应用访问地址
security.app.url=http://localhost:${server.port}
# 不校验权限的接口
security.ignoring.ant.matchers=/test, /tmp
#应用访问地址
security.app.host.url=http://localhost:${server.port}
#应用登录地址
security.app.login.url=/login
#应用登出地址
security.app.logout.url=/logout
', '284362a1a6e61b750f1855bc9613ef4e', '2010-05-05 00:00:00', '2022-09-19 14:13:18', 'nacos', '172.19.0.1', 'U', 'dev', '');
INSERT INTO nacos.his_config_info (id, nid, data_id, group_id, app_name, content, md5, gmt_create, gmt_modified, src_user, src_ip, op_type, tenant_id, encrypted_data_key) VALUES (1, 4, 'demo-sys-public', 'DEMO', '', 'server.port=10002
server.address = 0.0.0.0
debug=true
# root 日志级别以WARN级别输出
logging.level.root=INFO
# springframework.web日志以DEBUG级别输出
logging.level.org.springframework.web=DEBUG
# 文件位置
logging.file.name=log/${spring.application.name}.log
logging.file.max-size=1MB
# 数据库配置基础信息
spring.datasource.url=jdbc:mysql://localhost:3306/demo?serverTimezone=UTC&characterEncoding=utf-8&useUnicode=true&useSSL=false
spring.datasource.username=root
spring.datasource.password=root
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
# 连接池中最大的活跃连接数
spring.datasource.tomcat.max-active=10
# MyBatis
# 全局地开启或关闭配置文件中的所有映射器已经配置的任何缓存，默认为 true
mybatis-plus.configuration.cache-enabled=false
# 这个配置会将执行的sql打印出来，在开发或测试的时候可以用
mybatis-plus.configuration.log-impl=org.apache.ibatis.logging.stdout.StdOutImpl
# CAS服务地址
security.cas.server.host.url=http://localhost:8080/cas
# CAS服务登录地址
security.cas.server.host.login_url=${security.cas.server.host.url}/login
# CAS服务登出地址
security.cas.server.host.logout_url=${security.cas.server.host.url}/logout?service=${security.app.host.url}
# security获取用户信息接口地址
security.remote.user.detail.url=http://localhost:10002/public
# 应用访问地址
security.app.url=http://localhost:${server.port}
# 不校验权限的接口
security.ignoring.ant.matchers=/test, /tmp
#应用访问地址
security.app.host.url=http://localhost:${server.port}
#应用登录地址
security.app.login.url=/login
#应用登出地址
security.app.logout.url=/logout
useLocalCache=true', '212a605440582fc78f2ace7000020179', '2010-05-05 00:00:00', '2022-09-19 14:42:14', 'nacos', '172.19.0.1', 'U', 'dev', '');
INSERT INTO nacos.his_config_info (id, nid, data_id, group_id, app_name, content, md5, gmt_create, gmt_modified, src_user, src_ip, op_type, tenant_id, encrypted_data_key) VALUES (0, 5, 'demo-log-web', 'DEMO', '', 'server.port=10002
server.address = 0.0.0.0
debug=true
# root 日志级别以WARN级别输出
logging.level.root=INFO
# springframework.web日志以DEBUG级别输出
logging.level.org.springframework.web=DEBUG
# 文件位置
logging.file.name=log/${spring.application.name}.log
logging.file.max-size=1MB
# 数据库配置基础信息
spring.datasource.url=jdbc:mysql://localhost:3306/demo?serverTimezone=UTC&characterEncoding=utf-8&useUnicode=true&useSSL=false
spring.datasource.username=root
spring.datasource.password=root
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
# 连接池中最大的活跃连接数
spring.datasource.tomcat.max-active=10
# MyBatis
# 全局地开启或关闭配置文件中的所有映射器已经配置的任何缓存，默认为 true
mybatis-plus.configuration.cache-enabled=false
# 这个配置会将执行的sql打印出来，在开发或测试的时候可以用
mybatis-plus.configuration.log-impl=org.apache.ibatis.logging.stdout.StdOutImpl
# CAS服务地址
security.cas.server.host.url=http://localhost:8080/cas
# CAS服务登录地址
security.cas.server.host.login_url=${security.cas.server.host.url}/login
# CAS服务登出地址
security.cas.server.host.logout_url=${security.cas.server.host.url}/logout?service=${security.app.host.url}
# security获取用户信息接口地址
security.remote.user.detail.url=http://localhost:10002/public
# 应用访问地址
security.app.url=http://localhost:${server.port}
# 不校验权限的接口
security.ignoring.ant.matchers=/test, /tmp
#应用访问地址
security.app.host.url=http://localhost:${server.port}
#应用登录地址
security.app.login.url=/login
#应用登出地址
security.app.logout.url=/logout
useLocalCache=true
spring.cloud.sentinel.transport.dashboard=localhost:8858
spring.cloud.sentinel.transport.client-ip=127.0.0.1', 'cf37b069f159a17a44c66030e5d1aa98', '2010-05-05 00:00:00', '2022-09-19 16:09:52', null, '172.19.0.1', 'I', 'dev', '');
INSERT INTO nacos.his_config_info (id, nid, data_id, group_id, app_name, content, md5, gmt_create, gmt_modified, src_user, src_ip, op_type, tenant_id, encrypted_data_key) VALUES (5, 6, 'demo-log-web', 'DEMO', '', 'server.port=10002
server.address = 0.0.0.0
debug=true
# root 日志级别以WARN级别输出
logging.level.root=INFO
# springframework.web日志以DEBUG级别输出
logging.level.org.springframework.web=DEBUG
# 文件位置
logging.file.name=log/${spring.application.name}.log
logging.file.max-size=1MB
# 数据库配置基础信息
spring.datasource.url=jdbc:mysql://localhost:3306/demo?serverTimezone=UTC&characterEncoding=utf-8&useUnicode=true&useSSL=false
spring.datasource.username=root
spring.datasource.password=root
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
# 连接池中最大的活跃连接数
spring.datasource.tomcat.max-active=10
# MyBatis
# 全局地开启或关闭配置文件中的所有映射器已经配置的任何缓存，默认为 true
mybatis-plus.configuration.cache-enabled=false
# 这个配置会将执行的sql打印出来，在开发或测试的时候可以用
mybatis-plus.configuration.log-impl=org.apache.ibatis.logging.stdout.StdOutImpl
# CAS服务地址
security.cas.server.host.url=http://localhost:8080/cas
# CAS服务登录地址
security.cas.server.host.login_url=${security.cas.server.host.url}/login
# CAS服务登出地址
security.cas.server.host.logout_url=${security.cas.server.host.url}/logout?service=${security.app.host.url}
# security获取用户信息接口地址
security.remote.user.detail.url=http://localhost:10002/public
# 应用访问地址
security.app.url=http://localhost:${server.port}
# 不校验权限的接口
security.ignoring.ant.matchers=/test, /tmp
#应用访问地址
security.app.host.url=http://localhost:${server.port}
#应用登录地址
security.app.login.url=/login
#应用登出地址
security.app.logout.url=/logout
useLocalCache=true
spring.cloud.sentinel.transport.dashboard=localhost:8858
spring.cloud.sentinel.transport.client-ip=127.0.0.1', 'cf37b069f159a17a44c66030e5d1aa98', '2010-05-05 00:00:00', '2022-09-19 16:35:18', 'nacos', '172.19.0.1', 'U', 'dev', '');
INSERT INTO nacos.his_config_info (id, nid, data_id, group_id, app_name, content, md5, gmt_create, gmt_modified, src_user, src_ip, op_type, tenant_id, encrypted_data_key) VALUES (0, 7, 'demo-log-server', 'DEMO', '', 'server.port=10001
server.address = 0.0.0.0
debug=true
# root 日志级别以WARN级别输出
logging.level.root=INFO
# springframework.web日志以DEBUG级别输出
logging.level.org.springframework.web=DEBUG
# 文件位置
logging.file.name=log/${spring.application.name}.log
logging.file.max-size=1MB
# 数据库配置基础信息
spring.datasource.url=jdbc:mysql://localhost:3306/demo?serverTimezone=UTC&characterEncoding=utf-8&useUnicode=true&useSSL=false
spring.datasource.username=root
spring.datasource.password=root
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
# 连接池中最大的活跃连接数
spring.datasource.tomcat.max-active=10
# MyBatis
# 全局地开启或关闭配置文件中的所有映射器已经配置的任何缓存，默认为 true
mybatis-plus.configuration.cache-enabled=false
# 这个配置会将执行的sql打印出来，在开发或测试的时候可以用
mybatis-plus.configuration.log-impl=org.apache.ibatis.logging.stdout.StdOutImpl
#CAS服务地址
security.cas.server.host.url=http://10.10.11.166:8080/sgst-admin-cas
#CAS服务登录地址
security.cas.server.host.login_url=${security.cas.server.host.url}/login
#CAS服务登出地址
security.cas.server.host.logout_url=${security.cas.server.host.url}/logout?service=${security.app.host.url}
#应用访问地址
security.app.host.url=http://localhost:${server.port}
#应用登录地址
security.app.login.url=/login
#应用登出地址
security.app.logout.url=/logout
spring.cloud.sentinel.transport.dashboard=192.168.100.31:8858
spring.cloud.sentinel.transport.client-ip=192.168.100.31', '7b6d2211d1536c1beefc3509baf143d1', '2010-05-05 00:00:00', '2022-09-19 16:35:36', null, '172.19.0.1', 'I', 'dev', '');
INSERT INTO nacos.his_config_info (id, nid, data_id, group_id, app_name, content, md5, gmt_create, gmt_modified, src_user, src_ip, op_type, tenant_id, encrypted_data_key) VALUES (7, 8, 'demo-log-server', 'DEMO', '', 'server.port=10001
server.address = 0.0.0.0
debug=true
# root 日志级别以WARN级别输出
logging.level.root=INFO
# springframework.web日志以DEBUG级别输出
logging.level.org.springframework.web=DEBUG
# 文件位置
logging.file.name=log/${spring.application.name}.log
logging.file.max-size=1MB
# 数据库配置基础信息
spring.datasource.url=jdbc:mysql://localhost:3306/demo?serverTimezone=UTC&characterEncoding=utf-8&useUnicode=true&useSSL=false
spring.datasource.username=root
spring.datasource.password=root
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
# 连接池中最大的活跃连接数
spring.datasource.tomcat.max-active=10
# MyBatis
# 全局地开启或关闭配置文件中的所有映射器已经配置的任何缓存，默认为 true
mybatis-plus.configuration.cache-enabled=false
# 这个配置会将执行的sql打印出来，在开发或测试的时候可以用
mybatis-plus.configuration.log-impl=org.apache.ibatis.logging.stdout.StdOutImpl
#CAS服务地址
security.cas.server.host.url=http://10.10.11.166:8080/sgst-admin-cas
#CAS服务登录地址
security.cas.server.host.login_url=${security.cas.server.host.url}/login
#CAS服务登出地址
security.cas.server.host.logout_url=${security.cas.server.host.url}/logout?service=${security.app.host.url}
#应用访问地址
security.app.host.url=http://localhost:${server.port}
#应用登录地址
security.app.login.url=/login
#应用登出地址
security.app.logout.url=/logout
spring.cloud.sentinel.transport.dashboard=192.168.100.31:8858
spring.cloud.sentinel.transport.client-ip=192.168.100.31', '7b6d2211d1536c1beefc3509baf143d1', '2010-05-05 00:00:00', '2022-09-19 16:37:46', 'nacos', '172.19.0.1', 'U', 'dev', '');