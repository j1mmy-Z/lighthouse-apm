package com.jimmy.lighthouse.apm.agent.plugin.interceptor;

import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;

/**
 * @author Jimmy mailto:835490943@qq.com
 * @version 1.0.0
 * @since 2022-01-23
 *
 * 实例方法拦截点定义
 */
public interface InstanceMethodInterceptorDefine {

    /**
     * 实例方法匹配器
     *
     * @return 匹配器
     */
    ElementMatcher<MethodDescription> getMethodMatcher();

    /**
     * 实例方法拦截器的全类名
     *
     * @return 拦截器全类名
     * @see InstanceMethodInterceptor
     */
    String getMethodInterceptor();

    /**
     * 是否重写参数
     */
    boolean isOverrideArgs();
}
