# SpringBoot后端补充规范
*(与全局提示词配合使用)*

---

## 🔗 框架能力查询

**在编写 Spring Boot 应用代码时**,请注意:

### 必须先查阅 `后端脚手架.md` 的场景

- ✅ 需要配置**全局异常处理器**(GlobalExceptionHandler)
- ✅ 需要配置**统一返回格式包装**(ResultWrapper)
- ✅ 需要配置**跨域、拦截器、过滤器**等 Web 层组件
- ✅ 需要集成**Redis、Elasticsearch、Sa-Token、Sentinel**等第三方组件
- ✅ 需要配置**MyBatis-Plus**(分页插件、逻辑删除、自动填充等)
- ✅ 需要配置**接口文档**(SpringDoc/Swagger)
- ✅ 需要**配置文件加密**(Jasypt)

### 无需查阅的场景

- ❌ 定义业务 Controller、Service、Mapper
- ❌ 编写 Spring Boot 启动类或配置类(非框架集成相关)

### 📖 查阅方式 (优雅降级策略)

按以下优先级读取 `后端脚手架.md`:

1. **本地文件** (优先): `doc/dev/提示词/专业提示词/后端脚手架.md`
2. **GitHub** (本地不存在时): `https://raw.githubusercontent.com/fanzezhen/fun-framework-java/master/doc/dev/提示词/专业提示词/后端脚手架.md`
3. **Gitee** (GitHub 访问超时/失败时): `https://gitee.com/fanzezhen/fun-framework-java/raw/master/doc/dev/提示词/专业提示词/后端脚手架.md`

> 💡 **提示**: 找到对应模块后 → 查看框架模块的 README 了解详细用法

---
