package com.jimmy.lighthouse.apm.agent.plugin.interceptor;

import com.jimmy.lighthouse.apm.agent.plugin.enhancement.LighthouseEnhanced;
import com.jimmy.lighthouse.apm.agent.plugin.loader.MethodInterceptorLoader;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.implementation.bind.annotation.*;

/**
 * @author Jimmy mailto:835490943@qq.com
 * @version 1.0.0
 * @since 2022-01-23
 * 构造器切面
 */
@Slf4j
public class ConstructorPointCut {

    private ConstructorInterceptor constructorInterceptor;

    public ConstructorPointCut(String constructorInterceptorClassName, ClassLoader classLoader) {
        try {
            constructorInterceptor = (ConstructorInterceptor) MethodInterceptorLoader.load(constructorInterceptorClassName, classLoader);
        } catch (Exception e) {
            log.error("Load constructorInterceptor fail,class name={}", constructorInterceptorClassName);
        }
    }

    @RuntimeType
    public void interceptor(@This Object obj, @AllArguments Object[] args) {
        LighthouseEnhanced instance = (LighthouseEnhanced) obj;
        try {
            constructorInterceptor.afterConstruct(instance, args);
        } catch (Throwable throwable) {
            log.error("class {} constructor after construct fail", obj.getClass(), throwable);
        }
    }

}
