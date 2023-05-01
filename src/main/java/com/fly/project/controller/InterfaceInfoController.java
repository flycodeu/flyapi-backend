package com.fly.project.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fly.flyapiclientsdk.client.FlyApiClient;
import com.fly.flyapicommon.model.entity.InterfaceInfo;
import com.fly.flyapicommon.model.entity.User;
import com.fly.project.model.dto.interfaceinfo.InterfaceInfoInvokeRequest;

import com.fly.project.annotation.AuthCheck;
import com.fly.project.common.*;

import com.fly.project.constant.CommonConstant;
import com.fly.project.exception.BusinessException;
import com.fly.project.model.dto.interfaceinfo.InterfaceInfoAddRequest;
import com.fly.project.model.dto.interfaceinfo.InterfaceInfoQueryRequest;
import com.fly.project.model.dto.interfaceinfo.InterfaceInfoUpdateRequest;

import com.fly.project.model.enums.InterfaceInfoStatusEnum;
import com.fly.project.service.InterfaceInfoService;
import com.fly.project.service.UserService;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;

/**
 * 帖子接口
 *
 * @author yupi
 */
@RestController
@RequestMapping( "/interfaceInfo" )
@Slf4j
public class InterfaceInfoController {

    @Resource
    private InterfaceInfoService interfaceInfoService;

    @Resource
    private UserService userService;

    @Resource
    private FlyApiClient flyApiClient;
    // region 增删改查

    /**
     * 创建
     *
     * @param InterfaceInfoAddRequest
     * @param request
     * @return
     */
    @PostMapping( "/add" )
    public BaseResponse<Long> addInterfaceInfo(@RequestBody InterfaceInfoAddRequest InterfaceInfoAddRequest, HttpServletRequest request) {
        if (InterfaceInfoAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo InterfaceInfo = new InterfaceInfo();
        BeanUtils.copyProperties(InterfaceInfoAddRequest, InterfaceInfo);
        // 校验
        interfaceInfoService.validInterfaceInfo(InterfaceInfo, true);
        User loginUser = userService.getLoginUser(request);
        InterfaceInfo.setUserId(loginUser.getId());
        boolean result = interfaceInfoService.save(InterfaceInfo);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        long newInterfaceInfoId = InterfaceInfo.getId();
        return ResultUtils.success(newInterfaceInfoId);
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping( "/delete" )
    public BaseResponse<Boolean> deleteInterfaceInfo(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
        if (oldInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 仅本人或管理员可删除
        if (!oldInterfaceInfo.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = interfaceInfoService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新
     *
     * @param InterfaceInfoUpdateRequest
     * @param request
     * @return
     */
    @PostMapping( "/update" )
    public BaseResponse<Boolean> updateInterfaceInfo(@RequestBody InterfaceInfoUpdateRequest InterfaceInfoUpdateRequest,
                                                     HttpServletRequest request) {
        if (InterfaceInfoUpdateRequest == null || InterfaceInfoUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo InterfaceInfo = new InterfaceInfo();
        BeanUtils.copyProperties(InterfaceInfoUpdateRequest, InterfaceInfo);
        // 参数校验
        interfaceInfoService.validInterfaceInfo(InterfaceInfo, false);
        User user = userService.getLoginUser(request);
        long id = InterfaceInfoUpdateRequest.getId();
        // 判断是否存在
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
        if (oldInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 仅本人或管理员可修改
        if (!oldInterfaceInfo.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = interfaceInfoService.updateById(InterfaceInfo);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping( "/get" )
    public BaseResponse<InterfaceInfo> getInterfaceInfoById(long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo InterfaceInfo = interfaceInfoService.getById(id);
        return ResultUtils.success(InterfaceInfo);
    }

    /**
     * 获取列表（仅管理员可使用）
     *
     * @param InterfaceInfoQueryRequest
     * @return
     */
    @AuthCheck( mustRole = "admin" )
    @GetMapping( "/list" )
    public BaseResponse<List<InterfaceInfo>> listInterfaceInfo(InterfaceInfoQueryRequest InterfaceInfoQueryRequest) {
        InterfaceInfo InterfaceInfoQuery = new InterfaceInfo();
        if (InterfaceInfoQueryRequest != null) {
            BeanUtils.copyProperties(InterfaceInfoQueryRequest, InterfaceInfoQuery);
        }
        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>(InterfaceInfoQuery);
        queryWrapper.orderByDesc("id");
        List<InterfaceInfo> InterfaceInfoList = interfaceInfoService.list(queryWrapper);
        return ResultUtils.success(InterfaceInfoList);
    }

    /**
     * 分页获取列表
     *
     * @param InterfaceInfoQueryRequest
     * @param request
     * @return
     */
    @GetMapping( "/list/page" )
    public BaseResponse<Page<InterfaceInfo>> listInterfaceInfoByPage(InterfaceInfoQueryRequest InterfaceInfoQueryRequest, HttpServletRequest request) {
        if (InterfaceInfoQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo InterfaceInfoQuery = new InterfaceInfo();
        BeanUtils.copyProperties(InterfaceInfoQueryRequest, InterfaceInfoQuery);
        long current = InterfaceInfoQueryRequest.getCurrent();
        long size = InterfaceInfoQueryRequest.getPageSize();
        String sortField = InterfaceInfoQueryRequest.getSortField();
        String sortOrder = InterfaceInfoQueryRequest.getSortOrder();
        String description = InterfaceInfoQuery.getDescription();
        // content 需支持模糊搜索
        InterfaceInfoQuery.setDescription(null);
        // 限制爬虫
        if (size > 50) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>(InterfaceInfoQuery);
        queryWrapper.like(StringUtils.isNotBlank(description), "description", description);
        queryWrapper.orderBy(StringUtils.isNotBlank(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC), sortField);
        queryWrapper.orderByDesc("id");
        Page<InterfaceInfo> InterfaceInfoPage = interfaceInfoService.page(new Page<>(current, size), queryWrapper);
        return ResultUtils.success(InterfaceInfoPage);
    }

    // endregion

    /**
     * 接口上线
     *
     * @param idRequest 将id作为一个对象，方便使用
     * @param request   request请求
     * @return boolean
     */
    @PostMapping( "/online" )
    @AuthCheck( mustRole = "admin" )
    public BaseResponse<Boolean> onlineInterfaceInfo(@RequestBody IdRequest idRequest, HttpServletRequest request) throws UnsupportedEncodingException {
        if (idRequest == null || idRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long id = idRequest.getId();
        // 1. 校验接口是否存在
        InterfaceInfo oldInfo = interfaceInfoService.getById(id);
        if (oldInfo == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 2. 接口是否可以调用
        com.fly.flyapiclientsdk.model.User user = new com.fly.flyapiclientsdk.model.User();
        user.setUsername("fly");

        String username = flyApiClient.getNameByPostWithJson(user);

        if (StringUtils.isBlank(username)) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "接口验证失败");
        }

        // 3. 修改数据库字段
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        interfaceInfo.setId(id);
        interfaceInfo.setStatus((InterfaceInfoStatusEnum.ONLINE.getValue()));

        boolean result = interfaceInfoService.updateById(interfaceInfo);
        return ResultUtils.success(result);
    }


    /**
     * 接口下线
     *
     * @param idRequest 将id作为一个对象，方便使用
     * @param request   request请求
     * @return boolean
     */
    @PostMapping( "/offline" )
    @AuthCheck( mustRole = "admin" )
    public BaseResponse<Boolean> offlineInterfaceInfo(@RequestBody IdRequest idRequest, HttpServletRequest request) {
        if (idRequest == null || idRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        Long id = idRequest.getId();
        // 1. 校验接口是否存在
        InterfaceInfo oldInfo = interfaceInfoService.getById(id);
        if (oldInfo == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 3. 修改数据库字段
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        interfaceInfo.setId(id);
        interfaceInfo.setStatus(InterfaceInfoStatusEnum.OFFLINE.getValue());

        boolean result = interfaceInfoService.updateById(interfaceInfo);
        return ResultUtils.success(result);
    }

    public static final String HOST_INTERFACE = "http://localhost:8123/api";

    /**
     * 在线调用接口
     *
     * @param interfaceInfoInvokeRequest
     * @param request
     * @return
     */
    @PostMapping( "/invoke" )
    public BaseResponse<Object> InvokeInterfaceInfo(@RequestBody InterfaceInfoInvokeRequest interfaceInfoInvokeRequest, HttpServletRequest request) {
        if (interfaceInfoInvokeRequest == null || interfaceInfoInvokeRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        Long id = interfaceInfoInvokeRequest.getId();
        String userRequestParams = interfaceInfoInvokeRequest.getUserRequestParams();
        // 1. 校验接口是否存在
        InterfaceInfo oldInfo = interfaceInfoService.getById(id);
        // 找到名字
        String interfaceInfoName = oldInfo.getName();
        if (oldInfo == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 判断接口状态
        if (oldInfo.getStatus() == InterfaceInfoStatusEnum.OFFLINE.getValue()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口已关闭");
        }

        User loginUser = userService.getLoginUser(request);

        String accessKey = loginUser.getAccessKey();
        String secretKey = loginUser.getSecretKey();
        FlyApiClient temp = new FlyApiClient(accessKey, secretKey);
        String res = null;

        if (oldInfo.getUrl().equals(HOST_INTERFACE + "/story/getStory")) {
            try {
                res = temp.getFunnyStory();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else if (oldInfo.getUrl().equals(HOST_INTERFACE + "/name/user")) {
            Gson gson = new Gson();
            com.fly.flyapiclientsdk.model.User user = gson.fromJson(userRequestParams, com.fly.flyapiclientsdk.model.User.class);
            try {
                res = temp.getNameByPostWithJson(user);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return ResultUtils.success(res);
/*
        Gson gson = new Gson();
        com.fly.flyapiclientsdk.model.User user = gson.fromJson(userRequestParams, com.fly.flyapiclientsdk.model.User.class);
        String nameByPostWithJson = null;
        try {
            nameByPostWithJson = temp.getNameByPostWithJson(user);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return ResultUtils.success(nameByPostWithJson);
*/

/*        String funnyStory = null;
        try {
            funnyStory = temp.getFunnyStory();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return ResultUtils.success(funnyStory);*/

       /* Object result = reflectionInterface(FlyApiClient.class, interfaceInfoName, userRequestParams, accessKey, secretKey);
        //网关拦截对异常处理
        if (result.equals(GateWayErrorCode.FORBIDDEN.getCode())) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "调用次数已用尽");
        }
        return ResultUtils.success(result);*/
    }

    /**
     * 这段代码是一个反射方法，通过传入的反射类、方法名、参数、密钥等信息
     * ，来调用SDK中的对应方法。反射类和方法名作为必要参数，而参数、密钥等信息作为可选参数。
     *
     * @param reflectionClass 反射类
     * @param methodName      方法
     * @param parameter       参数
     * @param accessKey
     * @param secretKey
     * @return
     */


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
                    return result = method1.invoke(geapiClient, args);
                }
            }
        } catch (Exception e) {
            log.error("反射调用参数错误", e);
        }
        return result;
    }
    /**
     * 步骤： 根据传入的反射类构造该类的实例；
     * 获取该类中所有的方法；
     * 通过方法名筛选出需要调用的方法；
     * 获取该方法的参数类型，如果该方法没有参数则直接调用该方法；
     * 将传入的参数JSON字符串转换为方法参数类型，并调用该方法；
     * 返回方法的执行结果。
     */


}
