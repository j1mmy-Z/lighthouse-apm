package com.jimmy.lighthouse.apm.agent.plugin.core;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jimmy.lighthouse.apm.agent.plugin.loader.LighthouseClassLoader;
import com.jimmy.lighthouse.apm.agent.plugin.enhancement.AbstractPluginEnhancement;
import com.jimmy.lighthouse.apm.agent.plugin.matcher.AbstractJunction;
import com.jimmy.lighthouse.apm.agent.plugin.matcher.ClassMatcher;
import com.jimmy.lighthouse.apm.agent.plugin.matcher.FuzzyMatcher;
import com.jimmy.lighthouse.apm.agent.plugin.matcher.NameMatcher;
import com.jimmy.lighthouse.apm.common.exception.PluginException;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.description.NamedElement;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;

import static net.bytebuddy.matcher.ElementMatchers.isInterface;
import static net.bytebuddy.matcher.ElementMatchers.not;

/**
 * @author Jimmy mailto:835490943@qq.com
 * @version 1.0.0
 * @since 2022-01-23
 */
@Slf4j
public class PluginSearcher {

    /**
     * lighthouse-agent.jar
     */
    private static File lighthouseJar;

    /**
     * 插件名-插件类名
     */
    private static final HashMap<String, String> pluginMap = Maps.newHashMap();
    ;

    /**
     * 插件定义的实例
     */
    private static final List<AbstractPluginEnhancement> pluginEnhancements = Lists.newArrayList();

    /**
     * 类名匹配器map：类名-插件
     */
    private static final Map<String, List<AbstractPluginEnhancement>> classNameMatcherMap = Maps.newHashMap();

    /**
     * 模糊匹配器List
     */
    private static final List<AbstractPluginEnhancement> classFuzzyMatchers = Lists.newArrayList();

    /**
     * 加载插件
     */
    public void loadPlugins() {
        // 初始化类加载器
        LighthouseClassLoader.init();
        LighthouseClassLoader classLoader = LighthouseClassLoader.getInstance();

        try {
            // 读取配置文件
            Enumeration<URL> resources = classLoader.getResources("lighthouse-plugin.properties");
            // 通过自定义类加载器实例化所有插件定义
            while (resources.hasMoreElements()) {
                loadPlugin(resources.nextElement());
            }
            // 将插件进行缓存
            for (AbstractPluginEnhancement pluginEnhancement : pluginEnhancements) {
                ClassMatcher classMatcher = pluginEnhancement.enhancedClass();
                if (Objects.isNull(classMatcher)) {
                    continue;
                }
                if (classMatcher instanceof NameMatcher) {
                    NameMatcher nameMatcher = (NameMatcher) classMatcher;
                    List<AbstractPluginEnhancement> pluginEnhancements = classNameMatcherMap.get(nameMatcher.getClassName());
                    if (CollectionUtils.isEmpty(pluginEnhancements)) {
                        pluginEnhancements = Lists.newArrayList();
                        classNameMatcherMap.put(nameMatcher.getClassName(), pluginEnhancements);
                    }
                    pluginEnhancements.add(pluginEnhancement);
                } else {
                    classFuzzyMatchers.add(pluginEnhancement);
                }
            }
        } catch (Exception e) {
            log.error("load plugin fail", e);
        }
    }

    private void loadPlugin(URL url) {
        BufferedReader bufferedReader = null;
        String define;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()));
            while ((define = bufferedReader.readLine()) != null) {
                define = define.trim();
                if (define.length() <= 0 || StringUtils.startsWith(define, "#")) {
                    continue;
                }
                String[] kv = define.split("=");
                if (kv.length != 2) {
                    throw new PluginException("wrong define in lighthouse-plugin.properties,content:" + define);
                }
                String pluginName = kv[0];
                String pluginClassName = kv[1];
                pluginMap.put(pluginName, pluginClassName);
                // 实例化插件定义
                AbstractPluginEnhancement pluginInstance =
                        (AbstractPluginEnhancement) Class.forName(pluginClassName, true, LighthouseClassLoader.getInstance())
                                .newInstance();
                pluginEnhancements.add(pluginInstance);
            }
        } catch (Exception e) {
            log.error("load plugin fail,url:{}", url, e);
        } finally {
            try {
                if (Objects.nonNull(bufferedReader)) {
                    bufferedReader.close();
                }
            } catch (Exception e) {
                log.error("close io stream fail while load plugin,url:{}", url, e);
            }
        }
    }

    /**
     * 找到jar包的路径
     * jar:file:/D:/ideaProjects/lighthouse-apm/lighthouse-agent/target/lighthouse-agent.jar!/com/jimmy/lighthouse/apm/agent/plugin/PluginSearcher.class
     */
    public static File findPath() {
        if (Objects.isNull(lighthouseJar)) {
            lighthouseJar = findLighthouseJarFile();
        }
        return lighthouseJar;
    }

    public static File findLighthouseJarFile() {
        String url = PluginSearcher.class.getName().replaceAll("\\.", "/") + ".class";
        URL resource = ClassLoader.getSystemClassLoader().getResource(url);
        if (Objects.nonNull(resource)) {
            String jarPath = resource.toString();
            int index = jarPath.indexOf("!");
            if (index > -1) {
                jarPath = jarPath.substring("jar:file:".length(), index);
            } else {
                jarPath = jarPath.substring("file:".length(), jarPath.length() - url.length());
            }
            File file = new File(jarPath);
            if (file.exists()) {
                log.info("find lighthouse-agent.jar success,path={}", jarPath);
                return file.getParentFile();
            }
        }
        // 没有找到jar包抛出异常
        log.error("Can not find lighthouse-agent.jar");
        throw new PluginException("lighthouse-agent.jar not found");
    }

    /**
     * 根据之前加载的插件，构建匹配bytebuddy匹配
     */
    public ElementMatcher<? super TypeDescription> buildMatcher() {
        // 类名匹配
        ElementMatcher.Junction junction = new AbstractJunction<NamedElement>() {
            @Override
            public boolean match(NamedElement target) {
                return classNameMatcherMap.containsKey(target.getActualName());
            }
        };
        // 排除接口
        junction = junction.and(not(isInterface()));
        // 模糊匹配
        for (AbstractPluginEnhancement pluginEnhancement : classFuzzyMatchers) {
            ClassMatcher classMatcher = pluginEnhancement.enhancedClass();
            if (classMatcher instanceof FuzzyMatcher) {
                junction = junction.or(((FuzzyMatcher) classMatcher).buildJunction());
            }
        }
        return junction;
    }

    /**
     * 找到当前类匹配的插件
     */
    public List<AbstractPluginEnhancement> findEnhancement(TypeDescription typeDescription) {
        List<AbstractPluginEnhancement> list = Lists.newArrayList();
        String typeName = typeDescription.getTypeName();
        // 类名匹配
        if (classNameMatcherMap.containsKey(typeName)) {
            list.addAll(classNameMatcherMap.get(typeName));
        }
        // 模糊匹配
        for (AbstractPluginEnhancement pluginEnhancement : classFuzzyMatchers) {
            FuzzyMatcher classMatcher = (FuzzyMatcher) pluginEnhancement.enhancedClass();
            if (classMatcher.isMatch(typeDescription)) {
                list.add(pluginEnhancement);
            }
        }

        return list;
    }
}
