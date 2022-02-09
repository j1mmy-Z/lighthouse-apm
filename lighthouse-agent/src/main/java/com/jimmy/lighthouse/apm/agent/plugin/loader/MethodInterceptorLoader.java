package com.jimmy.lighthouse.apm.agent.plugin.loader;

import com.google.common.collect.Maps;
import com.jimmy.lighthouse.apm.agent.plugin.interceptor.MethodInterceptor;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Jimmy mailto:835490943@qq.com
 * @version 1.0.0
 * @since 2022-02-04
 * 用于加载插件中定义的方法拦截器
 * 插件由LighthouseClassloader加载，需要增强的类由AppClassloader加载，对于插件不可见
 * 因此需要将插件的类加载器的parent设置为加载需要增强的类的类加载器
 */
public class MethodInterceptorLoader {

    /**
     * 方法拦截器实例
     */
    private static final Map<String, MethodInterceptor> INTERCEPTOR_INSTANCE = Maps.newConcurrentMap();

    /**
     * 增强的目标类的类加载器（应用类加载器）-自定义类加载器
     */
    private static final Map<ClassLoader, ClassLoader> CLASS_LOADER_MAP = Maps.newHashMap();

    private static final ReentrantLock LOCK = new ReentrantLock();

    /**
     * 加载拦截器
     * @param interceptorClass 拦截器全类名
     * @param targetClassLoader 需要增强的目标类的类加载器
     * @return 拦截器实例
     */
    public static MethodInterceptor load(String interceptorClass, ClassLoader targetClassLoader) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        if (Objects.isNull(interceptorClass)) {
            targetClassLoader = MethodInterceptorLoader.class.getClassLoader();
        }
        String key = interceptorClass + "@" + targetClassLoader.hashCode();
        MethodInterceptor methodInterceptor = INTERCEPTOR_INSTANCE.get(key);
        if (Objects.isNull(methodInterceptor)) {
            ClassLoader classLoader;
            try {
                LOCK.lock();
                classLoader = CLASS_LOADER_MAP.get(targetClassLoader);
                if (Objects.isNull(classLoader)) {
                    // 自定义类加载器，parent指向加载目标类的类加载器
                    classLoader = new LighthouseClassLoader(targetClassLoader);
                    CLASS_LOADER_MAP.put(targetClassLoader, classLoader);
                }
            } finally {
                LOCK.unlock();
            }
            methodInterceptor = (MethodInterceptor) Class.forName(interceptorClass, true, classLoader).newInstance();
            INTERCEPTOR_INSTANCE.put(key, methodInterceptor);
        }

        return methodInterceptor;
    }
}
