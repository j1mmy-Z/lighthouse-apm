package com.jimmy.lighthouse.apm.agent.plugin.matcher;

import net.bytebuddy.description.type.TypeDescription;

/**
 * @author Jimmy mailto:835490943@qq.com
 * @version 1.0.0
 * @since 2022-02-03
 * 类匹配器
 */
public interface ClassMatcher {

    boolean isMatch(TypeDescription typeDescription);
}
