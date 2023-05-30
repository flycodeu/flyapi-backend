# 功能介绍
API接口调用平台，用户可以注册登录，可以使用接口，并且每次调用接口会进行统计。
管理员可以发布接口，下线接口，接入接口，以及可视化接口调用的情况。

# 模块划分
- flyapi-backend: API项目的主要后端部分，包含用户，接口，用户和接口的相关使用，包括(1)用户注册,登录等功能 (2)接口新增，删除，上线，下线等功能。(3) 用户调用接口，接口调用次数统计功能(4)用户权限鉴定，使用AOP来进行判断，在需要管理员才能进行操作的方法上面进行判断。
- flyapi-client-sdk: 开发者可以在 Starter 中定义一些默认配置，如果需要更改配置，则可以通过在应用程序中覆盖这些默认配置来实现。这样做可以提高应用程序的灵活性和可配置性，同时也减少了应用程序的代码复杂度。
- flyapi-common: 让方法、实体类在多个项目间复用，减少重复编写。包含实体类以及相应服务类的扩充。
- flyapi-gateway: 网关处理，包括鉴权，统一业务处理，路由转发等功能。
- flyapi-interface: 模拟接口，进行模拟调用。

# 设计思路
开发一个接口，普通用户可以进行调用，但是有调用次数限制，需要通过accessKey和secretKey来进行验证用户是否可以调用接口。
开发者可以发布接口给用户使用，需要满足一些发布条件。如下图
![image](https://user-images.githubusercontent.com/89577685/236359229-1838e13d-e5be-4c82-bf19-d832638e52d9.png)



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
- 自定义stater
- Spring Cloud GateWay
- nacos 注册中心
- dubbo 分布式框架服务


# 附一些运行界面图
## 接口管理
![image](https://user-images.githubusercontent.com/89577685/235394888-8c749671-6edc-438b-84a7-51c59233598b.png)

## 用户管理
![image](https://user-images.githubusercontent.com/89577685/235394908-89fbe26e-8909-4666-92a1-d165ecc51765.png)

## 接口分析
![image](https://user-images.githubusercontent.com/89577685/235394926-27047e70-0e9d-434a-9c87-539b1f0cb8fe.png)

## 调用接口
![image](https://user-images.githubusercontent.com/89577685/235394958-f8e9e8ea-5779-40a4-9b3f-c49fcf23693d.png)
![image](https://user-images.githubusercontent.com/89577685/235394966-88e5fd42-f5aa-4f52-a748-cd92dd5f2187.png)

## 已经实现的接口
1. 通过调用别人的API
![image](https://github.com/flybase1/flyapi-backend/assets/89577685/65fa1fc6-ba9b-4627-9455-4c32e1311014)

2. 自己通过爬虫(jsonp)爬取相应的图片
![image](https://github.com/flybase1/flyapi-backend/assets/89577685/afbc829c-bc2f-4f7a-8376-0379dd5c47fa)

3. 等待实现创建天气查询API

# 待实现的问题
1. 现在的接口发布以及接口设计都是由管理员完成，如何让其他人也可以发布自己的接口给别人使用。
2. 局部优化，例如https://api.btstu.cn/ ，丰富内容，用户易使用。
3. 由于目前接口量以及用户量很少，暂不打算采用redis来进行缓存。
4. 目前登录注册只有用户账号一个注册方法，可以扩充一下，比如邮箱，手机号。

# 推荐两个APi实现网站
- https://api.aa1.cn/ 
- https://api.btstu.cn/




