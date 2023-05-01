package com.fly.flyapicommon.service;


/**
* @author admin
* @description 针对表【user_interface_info(用户调用接口关系表)】的数据库操作Service
* @createDate 2023-04-23 09:20:13
*/
public interface InnerUserInterfaceInfoService  {

     /**
      * 调用接口统计次数
      * @param interfaceInfoId 调用接口id
      * @param userId  调用用户id
      * @return  调用次数
      */
     boolean invokeCount(long interfaceInfoId,long userId);

     /**
      * 是否可以调用次数
      * @param interfaceInfoId
      * @param userId
      * @return
      */
     boolean canInvoke(long interfaceInfoId,long userId);
}
