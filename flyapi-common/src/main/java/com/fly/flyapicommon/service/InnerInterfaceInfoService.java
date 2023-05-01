package com.fly.flyapicommon.service;


import com.fly.flyapicommon.model.entity.InterfaceInfo;

/**
 * @author admin
 * @description 针对表【interface_info(接口信息)】的数据库操作Service
 * @createDate 2023-04-20 15:07:26
 */
public interface InnerInterfaceInfoService {

    /**
     * 查询模拟接口是否存在（请求路径，请求方法，接口信息为空，表示不存在）
     * @param path
     * @param method
     * @return
     */
    InterfaceInfo getInterfaceInfo(String path, String method);
}
