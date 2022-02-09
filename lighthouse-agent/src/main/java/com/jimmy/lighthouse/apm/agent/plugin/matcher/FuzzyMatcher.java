package com.jimmy.lighthouse.apm.agent.plugin.matcher;

import net.bytebuddy.matcher.ElementMatcher;

/**
 * @author Jimmy mailto:835490943@qq.com
 * @version 1.0.0
 * @since 2022-02-03
 * 除了全类名直接匹配外的其他间接匹配方式
 */
public interface FuzzyMatcher extends ClassMatcher{

    ElementMatcher.Junction buildJunction();
}
