package com.lzh.game.framework.resource.data;

import com.lzh.game.framework.resource.Resource;
import com.lzh.game.framework.resource.config.GameResourceProperties;
import com.lzh.game.framework.resource.data.meta.ResourceMeta;
import com.lzh.game.framework.resource.data.meta.ResourceMetaManager;
import com.lzh.game.framework.utils.ClassScannerUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

@Slf4j
public class DefaultResourceMetaManager implements ResourceMetaManager, InitializingBean, DisposableBean {

    private final GameResourceProperties resourceProperties;

    public DefaultResourceMetaManager(GameResourceProperties resourceProperties) {
        this.resourceProperties = resourceProperties;
    }

    private final Map<String, Class<?>> nameCache = new HashMap<>();
    private final Map<Class<?>, ResourceMeta<?>> classCache = new HashMap<>();

    @Override
    public ResourceMeta<?> getResource(String resourceName) {
        return classCache.get(nameCache.get(resourceName));
    }

    @Override
    public ResourceMeta<?> getResource(Class<?> resourceClass) {
        return classCache.get(resourceClass);
    }

    @Override
    public Stream<String> getAllResourceName() {
        return nameCache.keySet().stream();
    }

    @Override
    public Stream<Class<?>> getAllResourceType() {
        return classCache.keySet().stream();
    }

    @Override
    public Iterator<ResourceMeta<?>> iterator() {

        return classCache.values().iterator();
    }

    private void loadModel(Class<?> type, ResourceNameStrategyStandard nameStrategyStandard) {

        ResourceMeta<?> model = parseToModel(type, nameStrategyStandard);

        nameCache.put(model.getResourceName(), type);
        classCache.put(type, model);
    }

    private ResourceMeta<?> parseToModel(Class<?> type, ResourceNameStrategyStandard nameStrategyStandard) {

        Resource resource = type.getAnnotation(Resource.class);
        String name = resource.name();

        if (!StringUtils.hasText(resource.name())) {
            String typeName = type.getSimpleName();
            name = typeName.substring(0,1).toUpperCase() + typeName.substring(1);
        }
        name = nameStrategyStandard.toTableName(name);
        if (nameCache.containsKey(name)) {
            throw new IllegalArgumentException("Already exist [" +  name +"] the resource name.");
        }
        // nameCache.put(name, type);
        ResourceMeta<?> model = ResourceMeta.of(type, name);
        if (Objects.isNull(model.getId())) {
            throw new IllegalArgumentException("[" + type.getName() + "] not defined @Id.");
        }
        return model;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        prepareLoad();
    }

    private void prepareLoad() throws Exception {
        StopWatch watch = new StopWatch();
        watch.start();
        log.info("Begin load resource.");
        try {
            Class<? extends ResourceNameStrategyStandard> standardType = resourceProperties.getNameStrategyStandard();
            ResourceNameStrategyStandard nameStrategyStandard = standardType.getDeclaredConstructor().newInstance();

            var list = ClassScannerUtils.scanPackagesWithAnnotatedType(resourceProperties.getResourceScannerPath(), Resource.class);
            if (list.isEmpty()) {
                log.warn("Not register any resource!!");
                return;
            }
            list.forEach(e -> loadModel(e, nameStrategyStandard));
        } finally {
            watch.stop();
            log.info("Load resource finish, use time: {}", watch.getTotalTimeMillis());
        }
    }

    @Override
    public void destroy() throws Exception {
        nameCache.clear();
        classCache.clear();
    }
}
