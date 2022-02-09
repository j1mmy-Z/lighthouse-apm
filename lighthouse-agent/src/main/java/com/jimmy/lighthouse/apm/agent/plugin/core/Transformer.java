package com.jimmy.lighthouse.apm.agent.plugin.core;

import com.jimmy.lighthouse.apm.agent.plugin.enhancement.AbstractPluginEnhancement;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.utility.JavaModule;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

/**
 * @author Jimmy mailto:835490943@qq.com
 * @version 1.0.0
 * @since 2022-02-03
 * <p>
 * 字节码增强
 */
@Slf4j
public class Transformer implements AgentBuilder.Transformer {

    private final PluginSearcher pluginSearcher;

    public Transformer(PluginSearcher pluginSearcher) {
        this.pluginSearcher = pluginSearcher;
    }

    /**
     * @param builder         拦截到的类的字节码
     * @param typeDescription 类
     * @param classLoader     加载拦截到的类的类加载器
     * @param module          jdk9中的module
     * @return 修改后的字节码
     */
    @Override
    public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassLoader classLoader, JavaModule module) {
        List<AbstractPluginEnhancement> list = pluginSearcher.findEnhancement(typeDescription);
        if (CollectionUtils.isNotEmpty(list)) {
            for (AbstractPluginEnhancement pluginEnhancement : list) {
                builder = pluginEnhancement.transform(typeDescription, builder, classLoader);
            }
            log.info("class {} is enhanced", typeDescription.getName());
        }
        return builder;
    }
}
