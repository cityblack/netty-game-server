package com.lzh.game.resource.data;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * {@link com.lzh.game.resource.Resource}
 * {@link com.lzh.game.resource.Id}
 * {@link com.lzh.game.resource.Index}
 * Resource entity model
 */
public class ResourceModel {

    private Getter id;

    private String resourceName;

    private Class<?> dataType;

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

    public Getter getId() {
        return id;
    }

    public void setId(Getter id) {
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

}
