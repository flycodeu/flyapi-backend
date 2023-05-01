package com.fly.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fly.flyapicommon.model.entity.InterfaceInfo;
import com.fly.flyapicommon.model.entity.User;
import com.fly.flyapicommon.model.entity.UserInterfaceInfo;


import com.fly.flyapicommon.service.InnerUserInterfaceInfoService;
import com.fly.project.common.ErrorCode;
import com.fly.project.exception.BusinessException;
import com.fly.project.mapper.UserInterfaceInfoMapper;
import com.fly.project.model.vo.UserVO;
import com.fly.project.service.UserInterfaceInfoService;
import org.springframework.stereotype.Service;

/**
 * @author admin
 * @description 针对表【user_interface_info(用户调用接口关系表)】的数据库操作Service实现
 * @createDate 2023-04-23 09:20:13
 */
@Service
public class UserInterfaceInfoServiceImpl extends ServiceImpl<UserInterfaceInfoMapper, UserInterfaceInfo>
        implements UserInterfaceInfoService {

    @Override
    public void validUserInterfaceInfo(UserInterfaceInfo userInterfaceInfo, boolean b) {
        if (userInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        if (b) {
            if (userInterfaceInfo.getId() <= 0 || userInterfaceInfo.getUserId() <= 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口用户不存在");
            }
        }

        if (userInterfaceInfo.getLeftNum() < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "剩余次数不能小于0");
        }


    }

    @Override
    public boolean invokeCount(long interfaceInfoId, long userId) {
        //todo 加上锁
        if (interfaceInfoId <= 0 || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UpdateWrapper<UserInterfaceInfo> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("interfaceInfoId", interfaceInfoId);
        updateWrapper.eq("userId", userId);
        updateWrapper.setSql("leftNum = leftNum-1,totalNum=totalNum+1");
        updateWrapper.gt("leftNum", 0);
        boolean isUpdate = this.update(updateWrapper);
        return isUpdate;
    }

    /** todo
     * 计算当前用户当前接口剩余使用次数
     * @param interfaceInfoId 接口id
     * @param userId 用户id
     * @return  返回剩余次数
     */
    @Override
    public Integer getUserIntegerCount(long interfaceInfoId, long userId) {
        if (interfaceInfoId <= 0 || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<UserInterfaceInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("interfaceInfoId", interfaceInfoId);
        queryWrapper.eq("userId", userId);
        UserInterfaceInfo userInterfaceInfo = getOne(queryWrapper);
        return userInterfaceInfo.getLeftNum();
    }


}
