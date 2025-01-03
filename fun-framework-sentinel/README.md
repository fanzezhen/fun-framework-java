common-sentinel
------------------------------------------   
基于文件的Pull模式sentinel客户端集成jar包

启用配置

    # sentinel-dashboard 地址
    spring.cloud.sentinel.transport.dashboard=192.168.100.31:8858
    # client IP地址
    spring.cloud.sentinel.transport.client-ip=192.168.100.31


启用的动态规则在src/main/resources/META-INF/services/com.alibaba.csp.sentinel.init.InitFunc中标记
    
    com.github.fanzezhen.fun.framework.sentinel.FileDataSourceInit
    com.github.fanzezhen.fun.framework.sentinel.NacosDataSourceInit