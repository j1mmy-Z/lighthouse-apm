package com.jimmy.lighthouse.apm.agent.plugin.enhancement;

import com.jimmy.lighthouse.apm.agent.plugin.interceptor.ConstructorInterceptorDefine;
import com.jimmy.lighthouse.apm.agent.plugin.interceptor.InstanceMethodInterceptorDefine;
import com.jimmy.lighthouse.apm.agent.plugin.interceptor.StaticMethodInterceptorDefine;
import com.jimmy.lighthouse.apm.agent.plugin.matcher.ClassMatcher;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.FieldAccessor;

import java.util.Objects;

import static net.bytebuddy.jar.asm.Opcodes.ACC_PRIVATE;
import static net.bytebuddy.jar.asm.Opcodes.ACC_VOLATILE;


/**
 * @author Jimmy mailto:835490943@qq.com
 * @version 1.0.0
 * @since 2022-01-23
 * <p>
 * 插件定义抽象
 */
@Slf4j
public abstract class AbstractPluginEnhancement {

    private static final String DYNAMIC_FiELD_NAME = "$lighthouse_file";

    /**
     * 字节码增强
     *
     * @param typeDescription 需要增强的类描述
     * @param builder         字节码
     * @param classLoader     需要增强的类的类加载器
     * @return 增强后的字节码
     */
    public DynamicType.Builder<?> transform(TypeDescription typeDescription,
                                            DynamicType.Builder<?> builder,
                                            ClassLoader classLoader) {
        // 拦截到的类名
        String transFormClassName = typeDescription.getName();
        // 当前增强器的类名
        String enhanceClassName = this.getClass().getName();

        log.info("enhancement {} transform {} start", enhanceClassName, transFormClassName);

        // 增强静态方法
        builder = this.enhanceStaticMethod(typeDescription, builder, classLoader);

        builder = addDynamicFileIfNecessary(typeDescription,builder);
        // 增构造方法
        builder = this.enhanceConstructor(typeDescription, builder, classLoader);
        // 增强实例方法
        builder = this.enhanceInstanceMethod(typeDescription, builder, classLoader);

        log.info("enhancement {} transform {} end", enhanceClassName, transFormClassName);

        return builder;
    }

    /**
     * 添加动态字段
     */
    private DynamicType.Builder<?> addDynamicFileIfNecessary(TypeDescription typeDescription,
                                                             DynamicType.Builder<?> builder) {
        ConstructorInterceptorDefine[] constructorInterceptorDefines = getConstructorInterceptorDefines();
        boolean hasConstructorInterceptors = Objects.nonNull(constructorInterceptorDefines) && constructorInterceptorDefines.length > 0;

        InstanceMethodInterceptorDefine[] instanceInterceptorDefines = getInstanceInterceptorDefines();
        boolean hasInstanceInterceptors = Objects.nonNull(instanceInterceptorDefines) && instanceInterceptorDefines.length > 0;

        // 没有实例、构造拦截器定义直接返回
        if (!hasConstructorInterceptors && !hasInstanceInterceptors) {
            return builder;
        }

        if (!typeDescription.isAssignableFrom(LighthouseEnhanced.class)) {
            builder = builder.implement(LighthouseEnhanced.class)
                    .intercept(FieldAccessor.ofField(DYNAMIC_FiELD_NAME))
                    .defineField(DYNAMIC_FiELD_NAME, Object.class, ACC_PRIVATE | ACC_VOLATILE);

        }
        return builder;
    }


    /**
     * 增强构造方法
     *
     * @param typeDescription 需要增强的类描述
     * @param builder         字节码
     * @param classLoader     需要增强的类的类加载器
     * @return 增强后的字节码
     */
    protected abstract DynamicType.Builder<?> enhanceConstructor(TypeDescription typeDescription,
                                                                 DynamicType.Builder<?> builder,
                                                                 ClassLoader classLoader);


    /**
     * 获取所有的实例方法拦截器定义
     */
    public abstract ConstructorInterceptorDefine[] getConstructorInterceptorDefines();

    /**
     * 增强实例方法
     *
     * @param typeDescription 需要增强的类描述
     * @param builder         字节码
     * @param classLoader     需要增强的类的类加载器
     * @return 增强后的字节码
     */
    protected abstract DynamicType.Builder<?> enhanceInstanceMethod(TypeDescription typeDescription,
                                                                    DynamicType.Builder<?> builder,
                                                                    ClassLoader classLoader);


    /**
     * 获取所有的实例方法拦截器定义
     */
    public abstract InstanceMethodInterceptorDefine[] getInstanceInterceptorDefines();

    /**
     * 增强静态方法
     *
     * @param typeDescription 需要增强的类描述
     * @param builder         字节码
     * @param classLoader     需要增强的类的类加载器
     * @return 增强后的字节码
     */
    protected abstract DynamicType.Builder<?> enhanceStaticMethod(TypeDescription typeDescription,
                                                                  DynamicType.Builder<?> builder,
                                                                  ClassLoader classLoader);

    /**
     * 获取所有的静态方法拦截器定义
     */
    public abstract StaticMethodInterceptorDefine[] getStaticInterceptorDefines();


    /**
     * 获取类匹配器
     */
    public abstract ClassMatcher enhancedClass();

}
