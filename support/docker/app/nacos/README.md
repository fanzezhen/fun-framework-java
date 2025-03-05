初始化数据库，导入初始化文件nacos-db.sql
执行脚本链接：https://github.com/alibaba/nacos/blob/master/config/src/main/resources/META-INF/nacos-db.sql

nacos启动参数


    -p 8848:8848           # 宿主机端口:容器端口
    --name nacos           # 容器名字
    --privileged=true      # 使用该参数，container内的root拥有真正的root权限, 否则，container内的root只是外部的一个普通用户权限
    --network host         # 设置属于该容器的网络
    --restart=always       # 总是重启，加上这句话之后，若重新启动Docker，该容器也会重新启动
    -e PREFER_HOST_MODE=hostname  # 是否支持 hostname，可选参数为hostname/ip，默认值是当前宿主机的ip
    -e MODE=standalone     # 使用 standalone模式（单机模式）,MODE值有cluster模式/standalone模式两种
    -e JVM_XMS=256m        # -Xms 为jvm启动时分配的内存，此处表⽰该进程只分配了256M内存
    -e JVM_XMX=256m        # -Xmx 为jvm运⾏过程中分配的最⼤内存，此处表⽰jvm进程最多只能够占⽤256M内存
    -d nacos/nacos-server  # 后台启动模式及使用的镜像  
    -v /Users/z7/microservice/nacos/logs:/home/nacos/logs
    -v /Users/z7/microservice/nacos/conf/application.properties:/home/nacos/conf/application.properties
    -v：挂载宿主机的一个目录, 持久化存储的关键所在，将主机目录挂载到容器对应目录，分别是：配置文件、日志文件
    --restart=always：容器自动启动参数，其值可以为[no,on-failure,always]
    no为默认值，表示容器退出时，docker不自动重启容器
    on-failure表示，若容器的退出状态非0，则docker自动重启容器,还可以指定重启次数，若超过指定次数未能启动容器则放弃
    always表示，只要容器退出，则docker将自动重启容器