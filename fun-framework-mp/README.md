fun-framework-cache-redis
------------------------------------------
Mybatis-plus配置模块

# 快速开始

## 1. 添加依赖

```xml

<dependency>
  <groupId>com.github.fanzezhen</groupId>
  <artifactId>fun-framework-mp</artifactId>
</dependency>
```

## 2. 使用注解

```java
package demo;

import com.github.fanzezhen.fun.framework.mp.EnableFunMpAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableFunMpAutoConfiguration
@SpringBootApplication
public class Application {
  public static void main(String[] args) {
    SpringApplication.run(SysWebApplication.class, args);
  }
}

```
# 功能介绍
## [base.entity](src%2Fmain%2Fjava%2Fcom%2Fgithub%2Ffanzezhen%2Ffun%2Fframework%2Fmp%2Fbase%2Fentity)：基础实体类
1. [BaseEntity](src%2Fmain%2Fjava%2Fcom%2Fgithub%2Ffanzezhen%2Ffun%2Fframework%2Fmp%2Fbase%2Fentity%2FBaseEntity.java)：基础实体模型，包含 主键、创建时间、创建人 字段
2. [BaseGenericEntity](src%2Fmain%2Fjava%2Fcom%2Fgithub%2Ffanzezhen%2Ffun%2Fframework%2Fmp%2Fbase%2Fentity%2FBaseGenericEntity.java)：常规实体模型，继承自[BaseEntity](src%2Fmain%2Fjava%2Fcom%2Fgithub%2Ffanzezhen%2Ffun%2Fframework%2Fmp%2Fbase%2Fentity%2FBaseEntity.java)，新增 删除标识、更新时间、更新人 字段
3. [BaseTenantEntity](src%2Fmain%2Fjava%2Fcom%2Fgithub%2Ffanzezhen%2Ffun%2Fframework%2Fmp%2Fbase%2Fentity%2Ftenant%2FBaseTenantEntity.java)：租户基础实体模型，继承自[BaseEntity](src%2Fmain%2Fjava%2Fcom%2Fgithub%2Ffanzezhen%2Ffun%2Fframework%2Fmp%2Fbase%2Fentity%2FBaseEntity.java)，新增 租户id 字段
4. [BaseTenantGenericEntity](src%2Fmain%2Fjava%2Fcom%2Fgithub%2Ffanzezhen%2Ffun%2Fframework%2Fmp%2Fbase%2Fentity%2Ftenant%2FBaseTenantGenericEntity.java)：租户常规实体模型，继承自[BaseTenantEntity](src%2Fmain%2Fjava%2Fcom%2Fgithub%2Ffanzezhen%2Ffun%2Fframework%2Fmp%2Fbase%2Fentity%2Ftenant%2FBaseTenantEntity.java)，新增 删除标识、更新时间、更新人 字段
## [generator](src%2Fmain%2Fjava%2Fcom%2Fgithub%2Ffanzezhen%2Ffun%2Fframework%2Fmp%2Fgenerator)：代码生成器
示例：[GeneratorTest.java](src%2Ftest%2Fjava%2Fcom%2Fgithub%2Ffanzezhen%2Ffun%2Fframework%2Fmp%2FGeneratorTest.java)
