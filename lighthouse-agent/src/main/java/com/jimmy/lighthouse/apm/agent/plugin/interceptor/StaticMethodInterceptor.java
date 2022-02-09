package com.jimmy.lighthouse.apm.agent.plugin.interceptor;

import java.lang.reflect.Method;

/**
 * @author Jimmy mailto:835490943@qq.com
 * @version 1.0.0
 * @since 2022-01-23
 */
public interface StaticMethodInterceptor extends MethodInterceptor {

    /**
     * 方法调用前置动作
     *
     * @param clazz          类
     * @param method         方法
     * @param args           参数
     * @param parameterTypes 参数类型
     * @param context        调用上下文
     */
    void before(Class<?> clazz, Method method, Object[] args, Class<?>[] parameterTypes,
                MethodInvokeContext context);

    /**
     * 方法调用后置动作
     *
     * @param clazz          类
     * @param method         方法
     * @param args           参数
     * @param parameterTypes 参数类型
     * @param returnVal      方法原始返回值
     * @return 方法最终返回值
     */
    Object after(Class<?> clazz, Method method, Object[] args, Class<?>[] parameterTypes,
                 Object returnVal);

    /**
     * 异常触发动作
     *
     * @param clazz          类
     * @param method         方法
     * @param args           参数
     * @param parameterTypes 参数类型
     * @param t              异常
     */
    void onException(Class<?> clazz, Method method, Object[] args, Class<?>[] parameterTypes,
                     Throwable t);
}
