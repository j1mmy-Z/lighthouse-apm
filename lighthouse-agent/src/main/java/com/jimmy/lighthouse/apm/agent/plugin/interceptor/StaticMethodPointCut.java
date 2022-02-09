package com.jimmy.lighthouse.apm.agent.plugin.interceptor;

import com.jimmy.lighthouse.apm.agent.plugin.loader.MethodInterceptorLoader;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.SuperCall;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

/**
 * @author Jimmy mailto:835490943@qq.com
 * @version 1.0.0
 * @since 2022-01-23
 * 静态方法切面
 */
@Slf4j
public class StaticMethodPointCut {

    private final String staticMethodInterceptorClassName;

    public StaticMethodPointCut(String staticMethodInterceptorClassName) {
        this.staticMethodInterceptorClassName = staticMethodInterceptorClassName;
    }

    /**
     * 静态方法字节码增强拦截器
     *
     * @param clazz    目标类
     * @param args     参数
     * @param method   方法
     * @param callable 调用
     * @return 当前静态方法返回值
     */
    public Object interceptor(@Origin Class<?> clazz,
                              @AllArguments Object[] args,
                              @Origin Method method,
                              @SuperCall Callable<?> callable) throws Throwable {

        StaticMethodInterceptor interceptor = (StaticMethodInterceptor) MethodInterceptorLoader.load(staticMethodInterceptorClassName, clazz.getClassLoader());

        MethodInvokeContext context = new MethodInvokeContext();

        try {
            interceptor.before(clazz, method, args, method.getParameterTypes(), context);
        } catch (Throwable throwable) {
            log.error("class {} static method {} before invoke fail", clazz.getName(), method.getName(), throwable);
        }

        Object res = null;

        try {
            if (context.isReturn()) {
                res = context.getReturnValue();
            } else {
                res = callable.call();
            }
        } catch (Throwable throwable) {
            try {
                interceptor.onException(clazz, method, args, method.getParameterTypes(), throwable);
            } catch (Throwable t) {
                log.error("class {} static method {} on exception fail", clazz.getName(), method.getName(), t);
            }
            throw throwable;
        } finally {
            try {
                interceptor.after(clazz, method, args, method.getParameterTypes(), res);
            } catch (Throwable throwable) {
                log.error("class {} static method {} after invoke fail", clazz.getName(), method.getName(), throwable);
            }
        }
        return res;
    }
}
