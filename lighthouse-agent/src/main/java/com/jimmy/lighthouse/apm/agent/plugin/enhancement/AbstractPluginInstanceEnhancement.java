package com.jimmy.lighthouse.apm.agent.plugin.enhancement;

import com.jimmy.lighthouse.apm.common.exception.PluginException;
import com.jimmy.lighthouse.apm.agent.plugin.interceptor.*;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

import static net.bytebuddy.matcher.ElementMatchers.isStatic;
import static net.bytebuddy.matcher.ElementMatchers.not;


/**
 * @author Jimmy mailto:835490943@qq.com
 * @version 1.0.0
 * @since 2022-01-23
 */
@Slf4j
abstract class AbstractPluginInstanceEnhancement extends AbstractPluginEnhancement {

    @Override
    protected DynamicType.Builder<?> enhanceInstanceMethod(TypeDescription typeDescription, DynamicType.Builder<?> builder, ClassLoader classLoader) {
        String transformClassName = typeDescription.getName();
        InstanceMethodInterceptorDefine[] instanceInterceptorDefines = getInstanceInterceptorDefines();
        if (Objects.isNull(instanceInterceptorDefines) || instanceInterceptorDefines.length <= 0) {
            log.warn("no instanceInterceptorDefines find for class {}", transformClassName);
            return builder;
        }
        for (InstanceMethodInterceptorDefine instanceMethodInterceptorDefine : instanceInterceptorDefines) {
            String interceptorClassName = instanceMethodInterceptorDefine.getMethodInterceptor();
            if (StringUtils.isEmpty(interceptorClassName)) {
                throw new PluginException("no InstanceMethodInterceptor define in " + instanceMethodInterceptorDefine.getClass().getName());
            }

            // 非静态方法匹配
            ElementMatcher.Junction<MethodDescription> junction
                    = not(isStatic()).and(instanceMethodInterceptorDefine.getMethodMatcher());

            // 声明式匹配
            if (instanceMethodInterceptorDefine instanceof DeclaredInstanceMethodInterceptorDefine) {
                junction = junction.and(ElementMatchers.isDeclaredBy(typeDescription));
            }

            if (instanceMethodInterceptorDefine.isOverrideArgs()) {
                // todo 实例方法参数覆盖拦截器
                return builder;
            } else {
                builder.method(junction)
                        .intercept(MethodDelegation.withDefaultConfiguration()
                                .to(new InstanceMethodPointCut(interceptorClassName, classLoader)));
            }
        }
        return builder;
    }

    @Override
    public abstract InstanceMethodInterceptorDefine[] getInstanceInterceptorDefines();


    @Override
    protected DynamicType.Builder<?> enhanceConstructor(TypeDescription typeDescription, DynamicType.Builder<?> builder, ClassLoader classLoader) {
        return builder;
    }

    @Override
    public ConstructorInterceptorDefine[] getConstructorInterceptorDefines() {
        return new ConstructorInterceptorDefine[0];
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
