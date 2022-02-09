package com.jimmy.lighthouse.apm.agent.plugin.loader;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jimmy.lighthouse.apm.agent.LightHouseAgent;
import com.jimmy.lighthouse.apm.agent.plugin.core.PluginSearcher;
import com.jimmy.lighthouse.apm.common.exception.PluginException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.jar.JarFile;

import static com.jimmy.lighthouse.apm.common.constant.LighthouseConstants.AGENT_PLUGIN_PATH;

/**
 * @author Jimmy mailto:835490943@qq.com
 * @version 1.0.0
 * @since 2022-01-23
 * 自定义类加载器，classpath为lighthouse-agent.jar包同目录下的plugins目录
 */
@Slf4j
public class LighthouseClassLoader extends ClassLoader {

    /**
     * classpath为lighthouse-agent.jar包同目录下的plugins目录
     */
    private static File classPathDirectory;

    /**
     * plugins目录下的所有插件jar包
     */
    private static final Map<String, JarFile> pluginJars = Maps.newHashMap();

    static {
        // 开启并行加载
        registerAsParallelCapable();
    }

    private static LighthouseClassLoader instance;


    public static void init() {
        // 单例
        if (Objects.isNull(instance)) {
            synchronized (LighthouseClassLoader.class) {
                if (Objects.isNull(instance)) {
                    instance = new LighthouseClassLoader(LightHouseAgent.class.getClassLoader());
                }
            }
        }
    }

    public LighthouseClassLoader(ClassLoader parentClassLoader) {
        super(parentClassLoader);
        File parent = PluginSearcher.findPath();
        classPathDirectory = new File(parent, AGENT_PLUGIN_PATH);
        if (!classPathDirectory.exists() || !classPathDirectory.isDirectory()) {
            log.warn("no [plugins] directory find in:{}", parent.getAbsolutePath());
        } else {
            // 缓存plugins目录下的所有插件jar包
            String[] list = classPathDirectory.list();
            if (Objects.nonNull(list) && list.length > 0) {
                for (String fileName : list) {
                    if (!StringUtils.endsWith(fileName, ".jar")) {
                        continue;
                    }
                    try {
                        pluginJars.put(parent.getAbsolutePath() + "/" + fileName,
                                new JarFile(new File(classPathDirectory, fileName)));
                    } catch (Exception e) {
                        log.error("add jar fail, jar name:{}", fileName);
                    }
                }
            }
        }
    }

    public static LighthouseClassLoader getInstance() {
        return instance;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        String className = name.replaceAll("\\.", "/").concat(".class");
        Collection<JarFile> jarFiles = pluginJars.values();
        if (CollectionUtils.isNotEmpty(jarFiles)) {
            for (JarFile jar : jarFiles) {
                if (Objects.isNull(jar.getJarEntry(className))) {
                    continue;
                }
                BufferedInputStream bufferedInputStream = null;
                ByteArrayOutputStream byteArrayOutputStream = null;
                try {
                    URL url = new URL("jar:file:" + jar.getName() + "!/" + className);
                    bufferedInputStream = new BufferedInputStream(url.openStream());
                    byteArrayOutputStream = new ByteArrayOutputStream();
                    byte[] data = new byte[1024];
                    int pos;
                    while ((pos = bufferedInputStream.read(data)) != -1) {
                        byteArrayOutputStream.write(data, 0, pos);
                    }
                    data = byteArrayOutputStream.toByteArray();
                    return defineClass(name, data, 0, data.length);
                } catch (Exception e) {
                    log.error("LighthouseClassLoader find class fail,className={}", className, e);
                } finally {
                    try {
                        if (Objects.nonNull(bufferedInputStream)) {
                            bufferedInputStream.close();
                        }
                        if (Objects.nonNull(byteArrayOutputStream)) {
                            byteArrayOutputStream.close();
                        }
                    } catch (IOException e) {
                        log.error("close io stream fail while load class {}", name, e);
                    }
                }
            }
        }
        throw new PluginException("class [" + name + "] not found in path:" + classPathDirectory.getAbsolutePath());
    }


    @Override
    protected Enumeration<URL> findResources(String name) throws IOException {
        ArrayList<URL> resources = Lists.newArrayList();
        for (Map.Entry<String, JarFile> entry : pluginJars.entrySet()) {
            if (Objects.isNull(entry.getValue().getEntry(name))) {
                continue;
            }
            resources.add(new URL("jar:file:" + entry.getKey() + "!/" + name));
        }
        Iterator<URL> iterator = resources.iterator();
        return new Enumeration<URL>() {
            @Override
            public boolean hasMoreElements() {
                return iterator.hasNext();
            }

            @Override
            public URL nextElement() {
                return iterator.next();
            }
        };
    }

    @Override
    protected URL findResource(String name) {
        if (MapUtils.isEmpty(pluginJars)) {
            return null;
        }
        for (Map.Entry<String, JarFile> entry : pluginJars.entrySet()) {
            if (Objects.isNull(entry.getValue().getEntry(name))) {
                continue;
            }
            String path = entry.getKey();
            try {
                return new URL("jar:file:" + path + "!/" + name);
            } catch (MalformedURLException e) {
                log.error("findResource {} in {} fail", name, path, e);
            }
        }
        return null;
    }
}
