package com.jimmy.lighthouse.apm.agent;

import com.jimmy.lighthouse.apm.agent.config.ConfigLoader;
import com.jimmy.lighthouse.apm.agent.dependency.event.LighthouseEventBus;
import com.jimmy.lighthouse.apm.agent.plugin.core.PluginSearcher;
import com.jimmy.lighthouse.apm.agent.plugin.core.TransformListener;
import com.jimmy.lighthouse.apm.agent.plugin.core.Transformer;
import com.jimmy.lighthouse.apm.agent.service.ServiceManager;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.dynamic.scaffold.TypeValidation;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.instrument.Instrumentation;

import static net.bytebuddy.matcher.ElementMatchers.nameContains;
import static net.bytebuddy.matcher.ElementMatchers.nameStartsWith;

/**
 * @author Jimmy mailto:835490943@qq.com
 * @version 1.0.0
 * @since 2022-01-22
 */
@Slf4j
public class LightHouseAgent {

    public static void premain(String args, Instrumentation instrumentation) {
        final PluginSearcher pluginSearcher = new PluginSearcher();
        // 加载配置项
        try {
            ConfigLoader.loadAgentArgs(args);
        } catch (IllegalAccessException e) {
            log.error("load config error", e);
        }

        // 加载所有插件
        pluginSearcher.loadPlugins();

        // 关闭类保存功能
        ByteBuddy byteBuddy = new ByteBuddy().with(TypeValidation.DISABLED);

        // 需要忽略的类
        new AgentBuilder.Default(byteBuddy).ignore(
                        nameStartsWith("net.bytebuddy.")
                                .or(nameStartsWith("org.slf4j"))
                                .or(nameStartsWith("ch.qos.logback"))
                                .or(nameStartsWith("org.groovy"))
                                .or(nameContains("javassist"))
                                .or(nameContains(".asm."))
                                .or(nameContains(".reflectasm."))
                                .or(nameStartsWith("sun.reflect"))
                                .or(nameStartsWith("com.jimmy.lighthouse"))
                                .or(ElementMatchers.isSynthetic()))
                // 类匹配
                .type(pluginSearcher.buildMatcher())
                // 增强逻辑
                .transform(new Transformer(pluginSearcher))
                // 替换保留模式
                .with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION)
                .with(new TransformListener())
                .installOn(instrumentation);

        ServiceManager.getInstance().startAllServices();

        Runtime.getRuntime()
                .addShutdownHook(new Thread(() -> {
                    ServiceManager.getInstance().shutdownAllServices();
                    LighthouseEventBus.shutDownEvenBus();
                }));
    }

}
