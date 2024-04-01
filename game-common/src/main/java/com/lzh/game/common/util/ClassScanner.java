package com.lzh.game.common.util;

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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

/**
 * 类扫描工具
 * @author zehong.l
 * @date 2023-05-31 15:45
 **/
@Slf4j
public class ClassScanner {

    public static List<Class<?>> scanPackage(String packageName, Predicate<Class<?>> classFilter) {
        List<Class<?>> result = new ArrayList<>();
        String classPath = ResourceLoader.CLASSPATH_URL_PREFIX
                + ClassUtils.convertClassNameToResourcePath(packageName)
                +"/**/*.class";
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
                        result.add(clazz);
                    }
                }
            }
            return result;
        } catch (Exception e) {
            log.error("", e);
            return Collections.emptyList();
        }
    }
}
