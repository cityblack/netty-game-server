package com.lzh.game.resource.data;

import com.lzh.game.resource.GameResourceProperties;
import com.lzh.game.resource.Id;
import com.lzh.game.resource.Index;
import com.lzh.game.resource.Resource;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;

@Slf4j
public class DefaultResourceModelFactory implements ResourceModelFactory, InitializingBean, DisposableBean {

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
        ResourceModel model = parseType(type);

        model.setResourceName(name);
        model.setDataType(type);
        model.setComparator(resource.comparator());
        return model;
    }

    private ResourceModel parseType(Class<?> type) {

        ResourceModel model = new ResourceModel();

        ReflectionUtils.doWithFields(type, field -> {

            if (field.isAnnotationPresent(Id.class)) {

                if (Objects.nonNull(model.getId())) {
                    throw new RuntimeException("[" + type.getName() + "] has multiple primaryKey");
                }
                String name = field.getName();
                IndexGetter idGetter = GetterBuild.createKeyIndex(field, name);
                model.setId(idGetter);
                model.addIndex(idGetter);

            } else if (field.isAnnotationPresent(Index.class)) {

                model.addIndex(GetterBuild.createFieldIndex(field));
            }

        });

        if (Objects.isNull(model.getId())) {
            throw new IllegalArgumentException("[" + type.getName() + "] not defined @Id.");
        }

        ReflectionUtils.doWithMethods(type, method -> {
            if (method.isAnnotationPresent(Index.class)) {
                model.addIndex(GetterBuild.createMethodIndex(method));
            }
        });
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
