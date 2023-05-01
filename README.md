# 功能介绍
API接口调用平台，用户可以注册登录，可以使用接口，并且每次调用接口会进行统计。
管理员可以发布接口，下线接口，接入接口，以及可视化接口调用的情况。

# 模块划分
- flyapi-backend: API项目的主要后端部分，包含用户，接口，用户和接口的相关使用，包括(1)用户注册,登录等功能 (2)接口新增，删除，上线，下线等功能。(3) 用户调用接口，接口调用次数统计功能 
- flyapi-client-sdk: 开发者可以在 Starter 中定义一些默认配置，如果需要更改配置，则可以通过在应用程序中覆盖这些默认配置来实现。这样做可以提高应用程序的灵活性和可配置性，同时也减少了应用程序的代码复杂度。
- flyapi-common: 让方法、实体类在多个项目间复用，减少重复编写。包含实体类以及相应服务类的扩充。
- flyapi-gateway: 网关处理，包括鉴权，统一业务处理，路由转发等功能。
- flyapi-interface: 模拟接口，进行模拟调用。

# 设计思路
开发一个接口，普通用户可以进行调用，但是有调用次数限制，需要通过accessKey和secretKey来进行验证用户是否可以调用接口。
开发者可以发布接口给用户使用，需要满足一些发布条件。如下图
![image](https://user-images.githubusercontent.com/89577685/235394387-e336258c-ceed-4eb3-bd42-6f5e121d7cda.png)



# 使用到的技术
- Spring Boot 2.7.0
- Spring MVC
- MySQL 驱动
- MyBatis
- MyBatis Plus
- Spring Session Redis 分布式登录
- Spring AOP
- Apache Commons Lang3 工具类
- Lombok 注解
- Swagger + Knife4j 接口文档
- Spring Boot 调试工具和项目处理器
- 全局请求响应拦截器（记录日志）
- 全局异常处理器
- 自定义错误码
- 封装通用响应类
- 用户注册、登录、搜索功能
- nacos 注册中心
- dubbo 分布式框架服务


访问 localhost:7529/api/doc.html 就能在线调试接口了，不需要前端配合啦~
