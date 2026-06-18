# Linux 运维补充规范

与全局提示词配合使用。

## 三层穿梭典型问题

### 磁盘空间不足
- 现象：`No space left on device`、写入失败、`df -h` 满
- 本质：日志未轮转、删除文件句柄未释放、inode 耗尽
- 排查：
  - `du -sh /* | sort -rh | head -20` 找大文件
  - `> /var/log/xxx.log` 清空而非删除
  - `lsof | grep deleted` 找持有句柄的进程并重启
  - `df -i` 检查 inode
- 方案：logrotate 自动轮转、定时清理 `/tmp` 与缓存、LVM 扩容

### CPU 负载过高
- 现象：响应慢、`top` load 高、单进程 100% CPU
- 本质：CPU 密集任务、上下文切换、中断风暴、GC 频繁
- 排查：
  - `top -c -o %CPU` 找进程
  - `top -H -p <pid>` + `jstack`/`pstack` 看线程栈
  - `renice 10 -p <pid>` 临时降优先级
- 方案：JVM 调优、cgroup/systemd CPU 配额、负载均衡、算法优化

### 网络连接问题
- 现象：`Connection refused/timed out`、服务调用失败
- 本质：端口未监听、防火墙拦截、TCP 连接耗尽（TIME_WAIT 过多）
- 排查：
  - `ss -tlnp | grep <port>` 检查监听
  - `telnet`/`curl`/`nc -zv host port` 测连通
  - `iptables -L -n` 或 `firewall-cmd --list-all` 查防火墙
- 方案：
  - `net.ipv4.tcp_tw_reuse=1` 重用 TIME_WAIT
  - `net.core.somaxconn` 增大监听队列
  - `net.ipv4.ip_local_port_range` 扩大端口范围
  - 服务发现 + 熔断降级

### 内存泄漏（OOM）
- 现象：`OutOfMemoryError: Java heap space`、OOM Killer、`dmesg` Out of memory
- 本质：对象未释放、缓存无限增长、JVM 配置不当、其他进程占用
- 排查：
  - `free -h`、`top -o %MEM`
  - `jmap -heap <pid>` 或 MAT 分析堆
- 方案：
  - JVM：`-Xms`/`-Xmx`、`-XX:+UseG1GC`、`-XX:MaxMetaspaceSize`
  - 代码：`WeakReference`/`SoftReference`、关闭 `InputStream`/`ResultSet`、避免静态集合无限增长
  - 系统：`vm.swappiness`、systemd `MemoryLimit`

### 权限问题
- 现象：`Permission denied`、读写失败、`sudo` 失败
- 本质：rwx 设置错、SELinux/AppArmor 拦截、用户不在 sudoers
- 排查：
  - `ls -la`、`stat filename`
  - `chmod 644 file`、`chown user:group file`
  - `sestatus`、`ausearch -m avc`
- 方案：ACL 细粒度（`setfacl`）、`visudo`、SELinux 策略、`setcap` 替代 root

### 服务启动失败
- 现象：`systemctl start` 失败、启动后立即退出
- 本质：依赖未就绪、配置错误、资源限制（ulimit/端口/fd）
- 排查：
  - `journalctl -u service-name -xe` 看日志
  - 手动前台启动观察
  - `systemctl list-dependencies` 查依赖
- 方案：
  - systemd：`After=`、`Restart=on-failure RestartSec=5s`、`LimitNOFILE=65535`
  - `EnvironmentFile` 加载环境变量
  - `ExecStartPre` 配置校验
  - 健康检查 + systemd notify

### Docker 容器问题
- 现象：容器立即退出、网络不通、磁盘占用大
- 本质：主进程退出、网络模式错误、镜像分层未清理
- 排查：
  - `docker logs <id>`
  - `docker inspect <id>`
  - `docker system prune -a --volumes`
- 方案：
  - 多阶段构建、合并 RUN、alpine 基础镜像
  - `HEALTHCHECK`、`--memory`/`--cpus` 限制
  - Volume 持久化、tmpfs 临时文件

## 思维提醒

每次处理 Linux 问题问自己：
1. 偶发还是系统性？（治标 vs 治本）
2. 有监控告警提前发现吗？
3. 能自动化避免重复劳动吗？
4. 文档完善吗？
5. 符合安全最佳实践吗？
