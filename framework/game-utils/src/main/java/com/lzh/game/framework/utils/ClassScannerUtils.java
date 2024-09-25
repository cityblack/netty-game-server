package com.lzh.game.framework.utils;

import org.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.util.*;

/**
 * @author zehong.l
 * @since 2024-06-13 16:47
 **/
public class ClassScannerUtils {

    public static Set<Class<?>> scanPackageWithAnnotatedType(String packageName, Class<? extends Annotation> clz) {
        if (Objects.isNull(packageName)) {
            return Collections.emptySet();
        }
        return new Reflections(packageName).getTypesAnnotatedWith(clz);
    }

    /**
     * Scan classes
     * @param packageNames package name
     * @param clz Target type
     * @return Classes that meet the requirements
     */
    public static Set<Class<?>> scanPackagesWithAnnotatedType(String[] packageNames, Class<? extends Annotation> clz) {
        if (Objects.isNull(packageNames) || packageNames.length == 0) {
            return Collections.emptySet();
        }
        Reflections reflections = null;
        for (String packageName : packageNames) {
            var newReflections = new Reflections(packageName);
            reflections = Objects.isNull(reflections) ?
                    newReflections : reflections.merge(newReflections);
        }
        return reflections.getTypesAnnotatedWith(clz);
    }

}
