package com.fly.project.model.vo;

import com.fly.flyapicommon.model.entity.InterfaceInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 接口信息封装
 */
@Data
@EqualsAndHashCode( callSuper = true )
public class InterfaceInfoVo extends InterfaceInfo {

    private static final long serialVersionUID = 6674184839723287258L;
    private Integer totalNum;


}
