线程组件
------------------------------------------------------------------------------------------------------------------------
错误码前缀 1003**

# 功能介绍

## 线程池工具

[PoolExecutors](src%2Fmain%2Fjava%2Fcom%2Fgithub%2Ffanzezhen%2Ffun%2Fframework%2Fcore%2Fthread%2FPoolExecutors.java)
用于快捷创建线程池，并为ThreadPoolTaskExecutor自动设置修饰器（[ThreadPoolTaskDecorator](src%2Fmain%2Fjava%2Fcom%2Fgithub%2Ffanzezhen%2Ffun%2Fframework%2Fcore%2Fthread%2Fdecorator%2FThreadPoolTaskDecorator.java)）

## 异步工具
[ExecutorHolder](src%2Fmain%2Fjava%2Fcom%2Fgithub%2Ffanzezhen%2Ffun%2Fframework%2Fcore%2Fthread%2FExecutorHolder.java)
用于异步并发执行任务，可以指定线程池