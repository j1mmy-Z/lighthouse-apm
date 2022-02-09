package com.jimmy.lighthouse.apm.agent.plugin.interceptor;

import com.jimmy.lighthouse.apm.agent.plugin.enhancement.LighthouseEnhanced;

/**
 * @author Jimmy mailto:835490943@qq.com
 * @version 1.0.0
 * @since 2022-01-23
 */
public interface ConstructorInterceptor extends MethodInterceptor{

    /**
     * 构造方法后置动作
     *
     * @param instance 当前实例
     * @param args     方法参数
     * @throws Throwable 异常
     */
    void afterConstruct(LighthouseEnhanced instance, Object[] args) throws Throwable;
}
