package com.jimmy.lighthouse.apm.agent.plugin.enhancement;


/**
 * @author Jimmy mailto:835490943@qq.com
 * @version 1.0.0
 * @since 2022-01-23
 *
 * 包含实例或构造增强的类会自动实现该接口，添加一个动态字段
 */
public interface LighthouseEnhanced {

    /**
     * 动态字段的get方法
     */
    Object getEnhancedDynamicField();

    /**
     * 动态字段的set方法
     */
    void setEnhancedDynamicField(Object value);
}
