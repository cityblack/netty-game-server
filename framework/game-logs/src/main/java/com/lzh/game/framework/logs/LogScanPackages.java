package com.lzh.game.framework.logs;

import com.lzh.game.framework.logs.anno.EnableLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author zehong.l
 * @since 2024-07-10 18:23
 **/
@Slf4j
public record LogScanPackages(String[] packageNames) {

    private static final String BEAN_NAME = LogScanPackages.class.getName();

    public static void registrar(BeanDefinitionRegistry registry, Collection<String> packages) {
        Assert.notNull(registry, "Registry is null.");
        Assert.notNull(packages, "PackageNames is null.");
        if (registry.containsBeanDefinition(BEAN_NAME)) {
            var definition = registry.getBeanDefinition(BEAN_NAME);
            var args = definition.getConstructorArgumentValues();
            args.addIndexedArgumentValue(0, mergePackages(args, packages));
        } else {
            var definition = new GenericBeanDefinition();
            definition.setBeanClass(LogScanPackages.class);
            definition.getConstructorArgumentValues().addIndexedArgumentValue(0, packages.toArray(String[]::new));
            definition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
            registry.registerBeanDefinition(BEAN_NAME, definition);
        }
    }

    private static String[] mergePackages(ConstructorArgumentValues values, Collection<String> packageNames) {
        var source = (String[]) values.getIndexedArgumentValue(0, String[].class).getValue();
        if (Objects.isNull(source) || source.length == 0) {
            return packageNames.toArray(String[]::new);
        }
        return Stream.concat(Arrays.stream(source), packageNames.stream()).toArray(String[]::new);
    }

    public static class Registrar implements ImportBeanDefinitionRegistrar {
        @Override
        public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
            registrar(registry, getPackagesToScan(metadata));
        }

        private Set<String> getPackagesToScan(AnnotationMetadata metadata) {
            var attributes = AnnotationAttributes.fromMap(metadata.getAnnotationAttributes(EnableLog.class.getName()));
            assert attributes != null;
            var base = attributes.getStringArray("basePackages");
            if (base.length == 0) {
                return Collections.emptySet();
            }
            return Arrays.stream(base).collect(Collectors.toSet());
        }
    }
}
