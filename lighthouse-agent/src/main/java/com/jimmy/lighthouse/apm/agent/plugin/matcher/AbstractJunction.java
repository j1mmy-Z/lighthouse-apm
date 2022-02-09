package com.jimmy.lighthouse.apm.agent.plugin.matcher;

import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.matcher.ElementMatcher;

/**
 * @author Jimmy mailto:835490943@qq.com
 * @version 1.0.0
 * @since 2022-02-03
 */
@Slf4j
public abstract class AbstractJunction<T> implements ElementMatcher.Junction<T> {

    @Override
    public <U extends T> Junction<U> and(ElementMatcher<? super U> other) {
        return new Conjunction<U>(this, other);
    }

    @Override
    public <U extends T> Junction<U> or(ElementMatcher<? super U> other) {
        return new Disjunction<>(this, other);
    }

    @Override
    public boolean matches(T target) {
        try {
            return match(target);
        } catch (Exception e) {
            log.error("match class {} fail", target, e);
        }
        return false;
    }

    protected abstract boolean match(T target);
}
