package com.fly.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fly.flyapicommon.model.entity.UserInterfaceInfo;
import com.fly.project.model.vo.UserVO;

public interface UserInterfaceInfoService extends IService<UserInterfaceInfo> {
    public void validUserInterfaceInfo(UserInterfaceInfo userInterfaceInfo, boolean b);

    public boolean invokeCount(long interfaceInfoId, long userId);

    public Integer getUserIntegerCount(long interfaceInfoId, long userId);
}
