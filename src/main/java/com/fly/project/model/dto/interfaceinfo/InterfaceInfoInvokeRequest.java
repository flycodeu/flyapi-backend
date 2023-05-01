package com.fly.project.model.dto.interfaceinfo;

import lombok.Data;

import java.io.Serializable;

/**
 * 测试调用参数
 */
@Data
public class InterfaceInfoInvokeRequest implements Serializable {

    private static final long serialVersionUID = 8017067705005926825L;
    /**
     * 接口id
     */
    private Long id;

    /**
     * 用户请求参数
     */
    private String userRequestParams;


}
