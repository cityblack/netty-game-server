package com.lzh.game.framework.resource.data;

import com.lzh.game.framework.resource.Resource;
import com.lzh.game.framework.resource.Id;
import com.lzh.game.framework.resource.Index;
import org.springframework.util.ReflectionUtils;

import java.util.*;
import java.util.function.BiConsumer;

/**
 * {@link Resource}
 * {@link Id}
 * {@link Index}
 * Resource entity model
 */
public class ResourceModel {

    private IndexGetter id;

    private String resourceName;

    private Class<?> dataType;
    // All index. include id index
    private Map<String, IndexGetter> index;

    public ResourceModel() {
        index = new HashMap<>();
    }

    public void addIndex(IndexGetter index) {
        this.index.put(index.name(), index);
    }

    public void forEachIndex(BiConsumer<String, IndexGetter> consumer) {
        index.forEach(consumer);
    }

    public void forEachIndexFilterId(BiConsumer<String, IndexGetter> consumer) {
        for (Map.Entry<String, IndexGetter> entry : index.entrySet()) {
            if (entry.getValue().equals(this.index)) {
                continue;
            }
            consumer.accept(entry.getKey(), entry.getValue());
        }
    }

    public IndexGetter getId() {
        return id;
    }

    public void setId(IndexGetter id) {
        this.id = id;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public Class<?> getDataType() {
        return dataType;
    }

    public void setDataType(Class<?> dataType) {
        this.dataType = dataType;
    }

    public Collection<IndexGetter> getIndex() {
        return index.values();
    }

    public static ResourceModel of(Class<?> type, String resourceName) {
        ResourceModel model = new ResourceModel();
        model.setDataType(type);
        model.setResourceName(resourceName);
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

        ReflectionUtils.doWithMethods(type, method -> {
            if (method.isAnnotationPresent(Index.class)) {
                model.addIndex(GetterBuild.createMethodIndex(method));
            }
        });

        return model;
    }
}
