package com.fly.project.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 请求id
 *
 * @author yupi
 */
@Data
public class IdRequest implements Serializable {
    /**
     * id
     */
    private Long id;

    private static final long serialVersionUID = 1L;
}