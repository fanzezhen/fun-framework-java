fun-framework-all
------------------------------------------   
用于简化引用所有核心模块

# 快速开始

## 1. 添加依赖

```xml
<dependency>
  <groupId>com.github.fanzezhen</groupId>
  <artifactId>fun-framework-all</artifactId>
</dependency>
```
## 2. 使用注解 @EnableFunCoreAutoConfiguration

```java
package demo;

import com.github.fanzezhen.fun.framework.all.EnableFunAutoConfiguration;
import com.github.fanzezhen.fun.framework.all.EnableFunAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableFunCoreAutoConfiguration
@SpringBootApplication
public class Application {
  // public static void main(String[] args) 
}

```