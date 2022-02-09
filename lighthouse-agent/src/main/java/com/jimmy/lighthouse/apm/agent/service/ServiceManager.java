package com.jimmy.lighthouse.apm.agent.service;

import com.google.common.collect.Maps;
import com.jimmy.lighthouse.apm.agent.plugin.loader.LighthouseClassLoader;

import java.util.Comparator;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * @author Jimmy mailto:835490943@qq.com
 * @version 1.0.0
 * @since 2022-02-05
 */
public class ServiceManager {

    private static final ServiceManager serviceManager = new ServiceManager();

    private final Map<Class, LighthouseService> serviceMap = Maps.newHashMap();

    private ServiceManager() {

    }

    public static ServiceManager getInstance() {
        return serviceManager;
    }

    public void startAllServices() {
        // spi加载服务
        loadService();
        // 初始化
        initService();
        // 启动
        startService();
    }

    private void initService() {
        serviceMap.values().stream().sorted(Comparator.comparingInt(LighthouseService::priority))
                .forEach(LighthouseService::init);
    }

    private void startService() {
        serviceMap.values().stream().sorted(Comparator.comparingInt(LighthouseService::priority))
                .forEach(LighthouseService::start);
    }

    private void loadService() {
        for (LighthouseService service : ServiceLoader.load(LighthouseService.class, LighthouseClassLoader.getInstance())) {
            serviceMap.put(service.getClass(), service);
        }
    }

    public void shutdownAllServices() {
        serviceMap.values().stream().sorted(Comparator.comparingInt(LighthouseService::priority))
                .forEach(LighthouseService::shutdown);
    }
}
