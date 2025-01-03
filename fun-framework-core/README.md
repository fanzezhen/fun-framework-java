common-core
------------------------------------------

启用配置

* @EnableCommonCoreConfig


    # AppCode
    project.code=${spring.application.name}
    # 版本
    project.version=@project.version@
    # 扫描包路径
    project.base.package=@project.groupId@
    # DTO包路径，用于使用Entity直接作为参数对数据库操作时自动识别OperateLog
    project.dto.packages=
    # 项目标题
    project.title=${spring.application.name}
    # 项目描述
    project.description=description
    # 项目联系人
    project.link.man=author
    # 项目联系地址
    project.link.url=https://github.com/fanzezhen/common/tree/master/common-springdoc
    # 项目联系邮箱
    project.link.email=e-mail
    # 项目许可
    project.license=The Apache License
    # 项目许可链接
    project.license.url=https://www.apache.org/licenses/LICENSE-2.0

MyBatis-Plus

    # 枚举配置， 支持统配符 * 或者 ; 分割
    mybatis-plus.type-enums-package=com.github.fanzezhen.fun.framework.mp.enums
    # 逻辑删除配置
    mybatis-plus.global-config.db-config.logic-delete-field: delFlag  # 全局逻辑删除的实体字段名(since 3.3.0)
    mybatis-plus.global-config.db-config.logic-delete-value: 1 # 逻辑已删除值(默认为 1)
    mybatis-plus.global-config.db-config.logic-not-delete-value: 0 # 逻辑未删除值(默认为 0)