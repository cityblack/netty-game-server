package com.lzh.game.framework.logs;

import com.lzh.game.framework.logs.anno.LogFacade;
import com.lzh.game.framework.utils.ClassScannerUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.StopWatch;

/**
 * @author zehong.l
 * @since 2024-07-17 10:30
 **/
@Slf4j
public class LogRegisterSupport {

    private final String[] packageNames;

    public LogRegisterSupport(String[] packageNames) {
        this.packageNames = packageNames;
    }

    public void registerRepositoriesIn(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        StopWatch watch = new StopWatch("Register log");
        watch.start();
        var list = ClassScannerUtils.scanPackage(packageNames, e -> e.isAnnotationPresent(LogFacade.class));
        for (Class<?> clazz : list) {
            if (registry.containsBeanDefinition(clazz.getName())) {
                continue;
            }
            var definition = new RootBeanDefinition();
            definition.setTargetType(clazz);
            definition.setBeanClass(JavassistLogFactory.class);
            definition.getConstructorArgumentValues().addIndexedArgumentValue(0, clazz);
            definition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
            registry.registerBeanDefinition(clazz.getName(), definition);
        }
        watch.stop();
        log.info("Register parse finish. use time: {}", watch.getTotalTimeMillis());
    }
}
