# 智慧社区项目介绍
微服务：nacos(注册中心、配置中心) + dubbo

# 1.主要业务项目：
- proprietor 业务端 (微服务)
- property 物业端 (微服务)
- lease 租房项目 (微服务)
  
# 2.数据处理项目：
- rabbitmq MQ统一处理
- payment-system 支付统一处理
- task 自动任务项目
- gateway 网关
- tx-manager 分布式事务

# 3.独立项目：
- admin（大后台）

# 4.公共代码模块：
- common (实体类全部在里面)

# 依赖情况说明
<h4>租房项目需要先启物业端</h4>

# 打包
整体打包 com.jsy.community(root) - Lifecycle - package

# 微服务项目 启动顺序
nacos ==> tx-manager ==> service模块 ==> web模块


## 采用微服务架构开发(最初构想)
- 注册中心: nacos
- 配置中心: nacos
- 服务调用: dubbo
- 服务总线: nacos
- 服务降级: sentinel 
- 全局事务: seata
- 负载均衡: ribbon
- 分布式日志: elk
- 网关:    gateway
