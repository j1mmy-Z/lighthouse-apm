package com.jimmy.lighthouse.apm.agent.plugin.enhancement;

import com.jimmy.lighthouse.apm.common.exception.PluginException;
import com.jimmy.lighthouse.apm.agent.plugin.interceptor.ConstructorInterceptorDefine;
import com.jimmy.lighthouse.apm.agent.plugin.interceptor.InstanceMethodInterceptorDefine;
import com.jimmy.lighthouse.apm.agent.plugin.interceptor.StaticMethodInterceptorDefine;
import com.jimmy.lighthouse.apm.agent.plugin.interceptor.StaticMethodPointCut;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

import static net.bytebuddy.matcher.ElementMatchers.isStatic;

/**
 * @author Jimmy mailto:835490943@qq.com
 * @version 1.0.0
 * @since 2022-01-23
 */
@Slf4j
abstract class AbstractPluginStaticEnhancement extends AbstractPluginEnhancement {

    protected DynamicType.Builder<?> enhanceStaticMethod(TypeDescription typeDescription,
                                                         DynamicType.Builder<?> builder,
                                                         ClassLoader classLoader) {

        String transformClassName = typeDescription.getName();
        StaticMethodInterceptorDefine[] staticInterceptorDefines = getStaticInterceptorDefines();
        if (Objects.isNull(staticInterceptorDefines) || staticInterceptorDefines.length <= 0) {
            log.warn("no StaticMethodInterceptorDefine find for class {}", transformClassName);
            return builder;
        }
        for (StaticMethodInterceptorDefine staticInterceptorDefine : staticInterceptorDefines) {
            String interceptorClassName = staticInterceptorDefine.getMethodInterceptor();
            if (StringUtils.isEmpty(interceptorClassName)) {
                throw new PluginException("no StaticMethodInterceptor define in " + staticInterceptorDefine.getClass().getName());
            }

            // 是否需要参数覆盖
            if (staticInterceptorDefine.isOverrideArgs()) {
                // todo 静态方法参数覆盖拦截器
                return builder;
            } else {
                builder.method(isStatic().and(staticInterceptorDefine.getMethodMatcher()))
                        .intercept(MethodDelegation.withDefaultConfiguration().to(
                                new StaticMethodPointCut(interceptorClassName)));
            }
        }
        return builder;
    }

    @Override
    public abstract StaticMethodInterceptorDefine[] getStaticInterceptorDefines();

    @Override
    protected DynamicType.Builder<?> enhanceConstructor(TypeDescription typeDescription, DynamicType.Builder<?> builder, ClassLoader classLoader) {
        return builder;
    }

    @Override
    public ConstructorInterceptorDefine[] getConstructorInterceptorDefines() {
        return new ConstructorInterceptorDefine[0];
    }

    @Override
    protected DynamicType.Builder<?> enhanceInstanceMethod(TypeDescription typeDescription, DynamicType.Builder<?> builder, ClassLoader classLoader) {
        return builder;
    }

    @Override
    public InstanceMethodInterceptorDefine[] getInstanceInterceptorDefines() {
        return new InstanceMethodInterceptorDefine[0];
    }

}
