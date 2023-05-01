package com.fly.project.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fly.flyapiclientsdk.client.FlyApiClient;
import com.fly.flyapicommon.model.entity.InterfaceInfo;
import com.fly.flyapicommon.model.entity.User;
import com.fly.flyapicommon.model.entity.UserInterfaceInfo;
import com.fly.flyapicommon.service.InnerUserInterfaceInfoService;
import com.fly.project.common.ErrorCode;
import com.fly.project.common.GateWayErrorCode;
import com.fly.project.exception.BusinessException;
import com.fly.project.service.impl.UserInterfaceInfoServiceImpl;
import com.google.gson.Gson;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户服务测试
 *
 * @author yupi
 */
@SpringBootTest
class UserServiceTest {

    @Resource
    private UserService userService;
    @Resource
    private InnerUserInterfaceInfoService inneruserInterfaceInfoService;
    @Resource
    private UserInterfaceInfoServiceImpl userInterfaceInfoService;
    @Resource
    private InterfaceInfoService interfaceInfoService;

    @Test
    void testAddUser() {
        User user = new User();
        boolean result = userService.save(user);
        System.out.println(user.getId());
        Assertions.assertTrue(result);
    }

    @Test
    void testUpdateUser() {
        User user = new User();
        boolean result = userService.updateById(user);
        Assertions.assertTrue(result);
    }

    @Test
    void testDeleteUser() {
        boolean result = userService.removeById(1L);
        Assertions.assertTrue(result);
    }

    @Test
    void testGetUser() {
        User user = userService.getById(1L);
        Assertions.assertNotNull(user);
    }

    @Test
    void userRegister() {
        String userAccount = "yupi";
        String userPassword = "";
        String checkPassword = "123456";
        try {
            long result = userService.userRegister(userAccount, userPassword, checkPassword);
            Assertions.assertEquals(-1, result);
            userAccount = "yu";
            result = userService.userRegister(userAccount, userPassword, checkPassword);
            Assertions.assertEquals(-1, result);
            userAccount = "yupi";
            userPassword = "123456";
            result = userService.userRegister(userAccount, userPassword, checkPassword);
            Assertions.assertEquals(-1, result);
            userAccount = "yu pi";
            userPassword = "12345678";
            result = userService.userRegister(userAccount, userPassword, checkPassword);
            Assertions.assertEquals(-1, result);
            checkPassword = "123456789";
            result = userService.userRegister(userAccount, userPassword, checkPassword);
            Assertions.assertEquals(-1, result);
            userAccount = "dogYupi";
            checkPassword = "12345678";
            result = userService.userRegister(userAccount, userPassword, checkPassword);
            Assertions.assertEquals(-1, result);
            userAccount = "yupi";
            result = userService.userRegister(userAccount, userPassword, checkPassword);
            Assertions.assertEquals(-1, result);
        } catch (Exception e) {

        }
    }

    @Test
    void testCount() {
        boolean b = inneruserInterfaceInfoService.invokeCount(23, 6);
        Assertions.assertTrue(b);
    }


    @Test
    void name() {
        UserInterfaceInfo uid = userInterfaceInfoService.getById(15L);
        List<InterfaceInfo> interfaceInfoList = interfaceInfoService.list();
        if (uid == null) {
            List<UserInterfaceInfo> userInterfaceInfoList = interfaceInfoList.stream()
                    .map(info -> {
                        UserInterfaceInfo userInterfaceInfo = new UserInterfaceInfo();
                        userInterfaceInfo.setInterfaceInfoId(info.getId());
                        userInterfaceInfo.setUserId(15L);
                        userInterfaceInfo.setLeftNum(20);
                        userInterfaceInfo.setTotalNum(20);
                        return userInterfaceInfo;
                    })
                    .collect(Collectors.toList());
            userInterfaceInfoService.saveBatch(userInterfaceInfoList);
        }
    }


    @Test
    void testLeftCount() {
        QueryWrapper<UserInterfaceInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("interfaceInfoId", 8L);
        queryWrapper.eq("userId", 15L);
        UserInterfaceInfo userInterfaceInfo = userInterfaceInfoService.getOne(queryWrapper);
        System.out.println(userInterfaceInfo);
    }

    @Test
    void canInvoke() {
        Integer userIntegerCount = userInterfaceInfoService.getUserIntegerCount(23L, 6L);
        if (userIntegerCount < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求次数已到上线");
        }
        System.out.println(userIntegerCount);
    }

    public Object reflectionInterface(Class<?> reflectionClass, String methodName, String parameter, String accessKey, String secretKey) {
        System.out.println(reflectionClass + " " + methodName + " " + parameter);
        //构造反射类的实例
        Object result = null;
        try {
            Constructor<?> constructor = reflectionClass.getDeclaredConstructor(String.class, String.class);
            //获取SDK的实例，同时传入密钥
            FlyApiClient geapiClient = (FlyApiClient) constructor.newInstance(accessKey, secretKey);
            //获取SDK中所有的方法
            Method[] methods = geapiClient.getClass().getMethods();
            //筛选出调用方法
            for (Method method : methods) {
                if (method.getName().equals(methodName)) {
                    //获取方法参数类型
                    Class<?>[] parameterTypes = method.getParameterTypes();
                    Method method1;
                    if (parameterTypes.length == 0) {
                        method1 = geapiClient.getClass().getMethod(methodName);
                        return method1.invoke(geapiClient);
                    }
                    method1 = geapiClient.getClass().getMethod(methodName, parameterTypes[0]);
                    //getMethod，多参会考虑重载情况获取方法,前端传来参数是JSON格式转换为String类型
                    //参数Josn化
                    Gson gson = new Gson();
                    Object args = gson.fromJson(parameter, parameterTypes[0]);
                    result = method1.invoke(geapiClient, args);
                    return result;
                }
            }
        } catch (Exception e) {
            System.out.println("反射错误");
        }
        return result;
    }

    @Test
    void reflect() {
        Object result = reflectionInterface(FlyApiClient.class, "public String getFunnyStory() throws UnsupportedEncodingException", "", "9e44946a265c996e05f6fad5bab87249", "4440120741c2ba54361754056a1c134e");
        //网关拦截对异常处理
        if (result.equals(GateWayErrorCode.FORBIDDEN.getCode())) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "调用次数已用尽");
        }
        System.out.println(result);
    }
}