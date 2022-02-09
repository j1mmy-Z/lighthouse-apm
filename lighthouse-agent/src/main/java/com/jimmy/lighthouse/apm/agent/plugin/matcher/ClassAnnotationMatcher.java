package com.jimmy.lighthouse.apm.agent.plugin.matcher;

import com.jimmy.lighthouse.apm.common.exception.PluginException;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.annotation.AnnotationList;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

/**
 * @author Jimmy mailto:835490943@qq.com
 * @version 1.0.0
 * @since 2022-02-03
 * 类注解匹配
 */
public class ClassAnnotationMatcher implements FuzzyMatcher {

    private List<String> annotations;

    private ClassAnnotationMatcher(List<String> annotations) {
        if (CollectionUtils.isEmpty(annotations)) {
            throw new PluginException("annotation collection cannot be empty");
        }
        this.annotations = annotations;
    }

    @Override
    public boolean isMatch(TypeDescription typeDescription) {
        AnnotationList declaredAnnotations = typeDescription.getDeclaredAnnotations();
        for (AnnotationDescription annotation : declaredAnnotations) {
            annotations.remove(annotation.getAnnotationType().getActualName());
        }
        return annotations.isEmpty();
    }

    @Override
    public ElementMatcher.Junction buildJunction() {
        ElementMatcher.Junction junction = null;
        for (String annotation : annotations) {
            if (junction == null) {
                junction = ElementMatchers.isAnnotatedWith(ElementMatchers.named(annotation));
            } else {
                junction.and(ElementMatchers.isAnnotatedWith(ElementMatchers.named(annotation)));
            }
        }
        return junction.and(ElementMatchers.not(ElementMatchers.isInterface()));
    }

    public static ClassAnnotationMatcher byClassAnnotation(List<String> annotations) {
        return new ClassAnnotationMatcher(annotations);
    }
}
