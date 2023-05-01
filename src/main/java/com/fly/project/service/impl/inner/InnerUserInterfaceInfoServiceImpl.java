package com.fly.project.service.impl.inner;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fly.flyapicommon.model.entity.UserInterfaceInfo;
import com.fly.flyapicommon.service.InnerUserInterfaceInfoService;
import com.fly.project.common.ErrorCode;
import com.fly.project.exception.BusinessException;
import com.fly.project.service.impl.UserInterfaceInfoServiceImpl;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

@DubboService
public class InnerUserInterfaceInfoServiceImpl implements InnerUserInterfaceInfoService {
    @Resource
    UserInterfaceInfoServiceImpl userInterfaceInfoService;

    @Override
    public boolean invokeCount(long interfaceInfoId, long userId) {
        return userInterfaceInfoService.invokeCount(interfaceInfoId, userId);
    }

    @Override
    public boolean canInvoke(long interfaceInfoId, long userId) {
        Integer userIntegerCount = userInterfaceInfoService.getUserIntegerCount(interfaceInfoId, userId);
        if (userIntegerCount < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求次数已到上线");
        }
        return userIntegerCount>0;
    }


}
