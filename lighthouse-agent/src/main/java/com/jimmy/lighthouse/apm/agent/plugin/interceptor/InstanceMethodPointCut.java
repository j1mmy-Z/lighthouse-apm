package com.jimmy.lighthouse.apm.agent.plugin.interceptor;

import com.jimmy.lighthouse.apm.agent.plugin.enhancement.LighthouseEnhanced;
import com.jimmy.lighthouse.apm.agent.plugin.loader.MethodInterceptorLoader;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.implementation.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

/**
 * @author Jimmy mailto:835490943@qq.com
 * @version 1.0.0
 * @since 2022-01-23
 * 实例方法切面
 */
@Slf4j
public class InstanceMethodPointCut {

    private InstanceMethodInterceptor instanceMethodInterceptor;

    public InstanceMethodPointCut(String instanceMethodInterceptorClassName, ClassLoader classLoader) {
        try {
            instanceMethodInterceptor = (InstanceMethodInterceptor) MethodInterceptorLoader.load(instanceMethodInterceptorClassName, classLoader);
        } catch (Exception e) {
            log.error("Load instanceMethodInterceptor fail,class name={}", instanceMethodInterceptorClassName);
        }
    }

    /**
     * 实例方法字节码增强拦截器
     *
     * @param obj      目标实例
     * @param args     方法参数
     * @param callable 调用
     * @param method   方法
     * @return 方法返回值
     */
    @RuntimeType
    public Object interceptor(
            @This Object obj,
            @AllArguments Object[] args,
            @SuperCall Callable<?> callable,
            @Origin Method method
    ) throws Throwable {
        LighthouseEnhanced instance = (LighthouseEnhanced) obj;

        MethodInvokeContext context = new MethodInvokeContext();

        try {
            instanceMethodInterceptor.before(instance, method, args, method.getParameterTypes(), context);
        } catch (Throwable throwable) {
            log.error("class {} instance method {} before invoke fail", obj.getClass(), method.getName(), throwable);
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
                instanceMethodInterceptor.onException(instance, method, args, method.getParameterTypes(), throwable);
            } catch (Throwable t) {
                log.error("class {} instance method {} on exception fail", obj.getClass(), method.getName(), t);
            }
            throw throwable;
        } finally {
            try {
                instanceMethodInterceptor.after(instance, method, args, method.getParameterTypes(), res);
            } catch (Throwable throwable) {
                log.error("class {} instance method {} after invoke fail", obj.getClass(), method.getName(), throwable);
            }
        }
        return res;
    }
}
