package com.fly.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fly.flyapicommon.model.entity.UserInterfaceInfo;

import java.util.List;


/**
* @author admin
* @description 针对表【user_interface_info(用户调用接口关系表)】的数据库操作Mapper
* @createDate 2023-04-23 09:20:13
* @Entity com.fly.project.model.entity.UserInterfaceInfo
*/
public interface UserInterfaceInfoMapper extends BaseMapper<UserInterfaceInfo> {

    //select interfaceInfoId,sum(totalNum) as totalNum from user_interface_info
    // group  by interfaceInfoId order by totalNum desc limit 3;

    List<UserInterfaceInfo> listTopInvokeInterfaceInfo(int limit);
}
