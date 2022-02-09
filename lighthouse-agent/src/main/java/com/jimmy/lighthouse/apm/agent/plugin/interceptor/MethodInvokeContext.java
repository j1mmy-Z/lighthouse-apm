package com.jimmy.lighthouse.apm.agent.plugin.interceptor;

import lombok.Getter;

/**
 * @author Jimmy mailto:835490943@qq.com
 * @version 1.0.0
 * @since 2022-01-23
 * 方法拦截上下文
 */
public class MethodInvokeContext {

    @Getter
    private boolean isReturn;

    @Getter
    private Object returnValue;

    public void overrideReturn(Object newRet) {
        this.isReturn = true;
        returnValue = newRet;
    }
}
