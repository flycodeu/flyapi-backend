package com.fly.flyaiinterface.aop;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * AOP统计次数
 */
@RestControllerAdvice
public class InvokeCountAop {

    // 切面触发时机       controller执行成功
    public void doInvokeCount(){

    }
}
