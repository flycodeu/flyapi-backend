package com.fly.flyapicommon.service;

import com.fly.flyapicommon.model.entity.User;


/**
 * 用户服务
 *
 * @author yupi
 */
public interface InnerUserService {
    /**
     * 数据库查询是否分配给用户密钥（ak，sk）
     *
     * @param accessKey
     * @return
     */
    User getInvokeUser(String accessKey);


}
