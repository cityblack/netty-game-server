package com.lzh.game.framework.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.classreading.SimpleMetadataReaderFactory;
import org.springframework.util.ClassUtils;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author zehong.l
 * @since 2024-06-13 16:47
 **/
@Slf4j
public class ClassScannerUtils {

    static final String DEFAULT_RESOURCE_PATTERN = "/**/*.class";

    private static void scanPackage0(Set<Class<?>> list, String packageName, Predicate<Class<?>> classFilter, Consumer<Class<?>> consumer) {
        String classPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
                + ClassUtils.convertClassNameToResourcePath(packageName)
                + DEFAULT_RESOURCE_PATTERN;
        try {
            ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
            MetadataReaderFactory metadataReaderFactory = new SimpleMetadataReaderFactory(resourcePatternResolver);
            Resource[] resources = new PathMatchingResourcePatternResolver().getResources(classPath);
            for (Resource resource : resources) {
                if (resource.isReadable()) {
                    MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
                    AnnotationMetadata metadata = metadataReader.getAnnotationMetadata();
                    String clazzName = metadata.getClassName();
                    if (clazzName.contains(ClassUtils.CGLIB_CLASS_SEPARATOR)) {
                        // inner class
                        continue;
                    }
                    Class<?> clazz = Thread.currentThread().getContextClassLoader().loadClass(clazzName);
                    if (list.contains(clazz)) {
                        continue;
                    }
                    if (classFilter.test(clazz)) {
                        list.add(clazz);
                        if (Objects.nonNull(consumer)) {
                            consumer.accept(clazz);
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("", e);
        }
    }

    private static void scanPackage(Set<Class<?>> list, String packageName, Predicate<Class<?>> classFilter) {
        scanPackage0(list, packageName, classFilter, null);
    }

    public static Set<Class<?>> scanPackage(String packageName, Predicate<Class<?>> classFilter) {
        Set<Class<?>> result = new HashSet<>();
        scanPackage(result, packageName, classFilter);
        return result;
    }

    public static Set<Class<?>> scanPackage(String[] packageName, Predicate<Class<?>> classFilter) {
        if (Objects.isNull(packageName) || packageName.length == 0) {
            return Collections.emptySet();
        }
        Set<Class<?>> result = new HashSet<>();
        for (String name : packageName) {
            scanPackage(result, name, classFilter);
        }
        return result;
    }

    public static void scanPackage(String[] packageNames, Predicate<Class<?>> classFilter, Consumer<Class<?>> consumer) {
        var set = new HashSet<Class<?>>();
        for (String packageName : packageNames) {
            scanPackage0(set, packageName, classFilter, consumer);
        }
    }

}
