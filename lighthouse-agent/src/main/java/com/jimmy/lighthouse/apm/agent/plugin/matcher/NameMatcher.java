package com.jimmy.lighthouse.apm.agent.plugin.matcher;

import lombok.Getter;
import net.bytebuddy.description.type.TypeDescription;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Jimmy mailto:835490943@qq.com
 * @version 1.0.0
 * @since 2022-02-03
 * 类名匹配
 */
public class NameMatcher implements ClassMatcher {

    @Getter
    private String className;

    private NameMatcher(String className) {
        this.className = className;
    }

    public static NameMatcher byName(String className) {
        return new NameMatcher(className);
    }

    @Override
    public boolean isMatch(TypeDescription typeDescription) {
        return StringUtils.equals(className, typeDescription.getName());
    }
}
