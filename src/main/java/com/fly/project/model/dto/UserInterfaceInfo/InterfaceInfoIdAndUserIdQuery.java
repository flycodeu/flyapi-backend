package com.fly.project.model.dto.UserInterfaceInfo;

import lombok.Data;

import java.io.Serializable;

@Data
public class InterfaceInfoIdAndUserIdQuery implements Serializable {

    /**
     * id
     */
    private Long userId;

    private Long interfaceInfoId;

    private static final long serialVersionUID = 1L;

}
