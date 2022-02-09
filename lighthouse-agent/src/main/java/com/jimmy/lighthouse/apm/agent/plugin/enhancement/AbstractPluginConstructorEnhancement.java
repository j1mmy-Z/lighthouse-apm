package com.jimmy.lighthouse.apm.agent.plugin.enhancement;

import com.jimmy.lighthouse.apm.common.exception.PluginException;
import com.jimmy.lighthouse.apm.agent.plugin.interceptor.ConstructorInterceptorDefine;
import com.jimmy.lighthouse.apm.agent.plugin.interceptor.ConstructorPointCut;
import com.jimmy.lighthouse.apm.agent.plugin.interceptor.InstanceMethodInterceptorDefine;
import com.jimmy.lighthouse.apm.agent.plugin.interceptor.StaticMethodInterceptorDefine;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.SuperMethodCall;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;


/**
 * @author Jimmy mailto:835490943@qq.com
 * @version 1.0.0
 * @since 2022-01-23
 */
@Slf4j
abstract class AbstractPluginConstructorEnhancement extends AbstractPluginEnhancement {

    @Override
    protected DynamicType.Builder<?> enhanceConstructor(TypeDescription typeDescription, DynamicType.Builder<?> builder, ClassLoader classLoader) {
        String transformClassName = typeDescription.getName();
        ConstructorInterceptorDefine[] constructorInterceptorDefines = getConstructorInterceptorDefines();
        if (Objects.isNull(constructorInterceptorDefines) || constructorInterceptorDefines.length <= 0) {
            log.warn("no constructorInterceptorDefines find for class {}", transformClassName);
            return builder;
        }
        for (ConstructorInterceptorDefine constructorInterceptorDefine : constructorInterceptorDefines) {
            String interceptorClassName = constructorInterceptorDefine.getConstructorInterceptor();
            if (StringUtils.isEmpty(interceptorClassName)) {
                throw new PluginException("no ConstructorInterceptor define in " + constructorInterceptorDefine.getClass().getName());
            }

            builder.constructor(constructorInterceptorDefine.getConstructorMatcher())
                    .intercept(SuperMethodCall.INSTANCE.andThen(MethodDelegation.withDefaultConfiguration()
                            .to(new ConstructorPointCut(interceptorClassName, classLoader))));
        }
        return builder;
    }

    @Override
    public abstract ConstructorInterceptorDefine[] getConstructorInterceptorDefines();


    @Override
    protected DynamicType.Builder<?> enhanceInstanceMethod(TypeDescription typeDescription, DynamicType.Builder<?> builder, ClassLoader classLoader) {
        return builder;
    }

    @Override
    public InstanceMethodInterceptorDefine[] getInstanceInterceptorDefines() {
        return new InstanceMethodInterceptorDefine[0];
    }

    @Override
    protected DynamicType.Builder<?> enhanceStaticMethod(TypeDescription typeDescription, DynamicType.Builder<?> builder, ClassLoader classLoader) {
        return builder;
    }

    @Override
    public StaticMethodInterceptorDefine[] getStaticInterceptorDefines() {
        return new StaticMethodInterceptorDefine[0];
    }
}
