package com.fly.project.service.impl;


import com.alibaba.nacos.shaded.com.google.gson.JsonSyntaxException;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fly.flyapiclientsdk.client.FlyApiClient;
import com.fly.flyapicommon.model.entity.InterfaceInfo;
import com.fly.flyapicommon.model.entity.User;
import com.fly.flyapicommon.model.enums.InterfaceInfoStatusEnum;
import com.fly.project.common.ErrorCode;
import com.fly.project.exception.BusinessException;
import com.fly.project.mapper.InterfaceInfoMapper;
import com.fly.project.model.dto.interfaceinfo.InterfaceInfoInvokeRequest;
import com.fly.project.service.InterfaceInfoService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;


/**
 * @author admin
 * @description 针对表【interface_info(接口信息)】的数据库操作Service实现
 * @createDate 2023-04-20 15:07:26
 */
@Service
public class InterfaceInfoServiceImpl extends ServiceImpl<InterfaceInfoMapper, InterfaceInfo>
        implements InterfaceInfoService {

    @Override
    public void validInterfaceInfo(InterfaceInfo interfaceInfo, boolean add) {
        if (interfaceInfo == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        Long id = interfaceInfo.getId();
        String name = interfaceInfo.getName();
        String description = interfaceInfo.getDescription();
        String url = interfaceInfo.getUrl();
        Integer status = interfaceInfo.getStatus();
        String method = interfaceInfo.getMethod();

        if (add) {
            if (StringUtils.isAllBlank(name)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR);
            }
        }

        if (StringUtils.isNotBlank(name) && name.length() > 50) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "调用名过长");
        }

        if (StringUtils.isNotBlank(description) && description.length() > 8102) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "描述过长");
        }
    }


}
