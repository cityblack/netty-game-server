package com.lzh.game.framework.resource.data;

import com.lzh.game.framework.resource.config.GameResourceProperties;
import com.lzh.game.framework.resource.Resource;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Stream;

@Slf4j
public class DefaultResourceModelFactory implements ResourceModelMeta, InitializingBean, DisposableBean {

    private final GameResourceProperties resourceProperties;

    public DefaultResourceModelFactory(GameResourceProperties resourceProperties) {
        this.resourceProperties = resourceProperties;
    }

    private Map<String, Class<?>> nameCache = new HashMap<>();
    private Map<Class<?>, ResourceModel> classCache = new HashMap<>();

    @Override
    public ResourceModel getResource(String resourceName) {
        return classCache.get(nameCache.get(resourceName));
    }

    @Override
    public ResourceModel getResource(Class<?> resourceClass) {
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
    public Iterator<ResourceModel> iterator() {

        return classCache.values().iterator();
    }

    private void loadModel(Class<?> type, ResourceNameStrategyStandard nameStrategyStandard) {

        ResourceModel model = parseToModel(type, nameStrategyStandard);

        nameCache.put(model.getResourceName(), type);
        classCache.put(type, model);
    }

    private ResourceModel parseToModel(Class<?> type, ResourceNameStrategyStandard nameStrategyStandard) {

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
        ResourceModel model = ResourceModel.of(type, name);
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
        Class<? extends ResourceNameStrategyStandard> standardType = resourceProperties.getNameStrategyStandard();
        ResourceNameStrategyStandard nameStrategyStandard = standardType.getDeclaredConstructor().newInstance();

        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forPackage(resourceProperties.getResourceScannerPath())));
        Set<Class<?>> set = reflections.getTypesAnnotatedWith(Resource.class);
        if (set.isEmpty()) {
            log.warn("Not register any resource!!");
            return;
        }
        set.stream().forEach(e -> loadModel(e, nameStrategyStandard));
    }

    @Override
    public void destroy() throws Exception {
        nameCache.clear();
        classCache.clear();
    }
}
