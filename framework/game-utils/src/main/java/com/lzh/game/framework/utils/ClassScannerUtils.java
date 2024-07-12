package com.lzh.game.framework.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.classreading.SimpleMetadataReaderFactory;
import org.springframework.util.ClassUtils;

import java.util.*;
import java.util.function.Predicate;

/**
 * @author zehong.l
 * @since 2024-06-13 16:47
 **/
@Slf4j
public class ClassScannerUtils {

    private static void scanPackage(Set<Class<?>> list, String packageName, Predicate<Class<?>> classFilter) {
        String classPath = ResourceLoader.CLASSPATH_URL_PREFIX
                + ClassUtils.convertClassNameToResourcePath(packageName)
                + "/**/*.class";
        try {
            ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
            MetadataReaderFactory metadataReaderFactory = new SimpleMetadataReaderFactory(resourcePatternResolver);
            Resource[] resources = new PathMatchingResourcePatternResolver().getResources(classPath);
            for (Resource resource : resources) {
                if (resource.isReadable()) {
                    MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
                    AnnotationMetadata metadata = metadataReader.getAnnotationMetadata();
                    String clazzName = metadata.getClassName();
                    if (clazzName.contains("$")) {
                        // inner class
                        continue;
                    }
                    Class<?> clazz = Thread.currentThread().getContextClassLoader().loadClass(clazzName);
                    if (classFilter.test(clazz)) {
                        list.add(clazz);
                    }
                }
            }
        } catch (Exception e) {
            log.error("", e);
        }
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
}
