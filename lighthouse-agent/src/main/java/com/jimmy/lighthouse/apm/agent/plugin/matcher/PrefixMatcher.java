package com.jimmy.lighthouse.apm.agent.plugin.matcher;

import com.jimmy.lighthouse.apm.common.exception.PluginException;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * @author Jimmy mailto:835490943@qq.com
 * @version 1.0.0
 * @since 2022-02-03
 * 前缀匹配
 */
public class PrefixMatcher implements FuzzyMatcher {

    private List<String> prefixes;

    private PrefixMatcher(List<String> prefixes) {
        if (CollectionUtils.isEmpty(prefixes)) {
            throw new PluginException("prefix collection cannot be empty");
        }
        this.prefixes = prefixes;
    }

    @Override
    public boolean isMatch(TypeDescription typeDescription) {
        for (String prefix : prefixes) {
            if (StringUtils.startsWith(typeDescription.getName(), prefix)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public ElementMatcher.Junction buildJunction() {
        ElementMatcher.Junction junction = null;
        for (String prefix : prefixes) {
            if (junction == null) {
                junction = ElementMatchers.nameStartsWith(prefix);
            } else {
                junction.or(ElementMatchers.nameStartsWith(prefix));
            }
        }
        return junction.and(ElementMatchers.not(ElementMatchers.isInterface()));
    }

    public static PrefixMatcher startWith(List<String> prefixes) {
        return new PrefixMatcher(prefixes);
    }
}
