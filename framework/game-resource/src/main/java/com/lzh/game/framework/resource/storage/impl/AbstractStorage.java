package com.lzh.game.framework.resource.storage.impl;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.lzh.game.framework.resource.data.load.ResourceLoadHandler;
import com.lzh.game.framework.resource.data.meta.ResourceMeta;
import com.lzh.game.framework.resource.data.meta.index.GetterBuild;
import com.lzh.game.framework.resource.data.meta.index.IndexGetter;
import com.lzh.game.framework.resource.reload.ResourceDataLoaded;
import com.lzh.game.framework.resource.storage.Storage;
import com.lzh.game.framework.utils.JsonUtils;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.*;

/**
 * @author zehong.l
 * @since 2024-08-20 11:25
 **/
public abstract class AbstractStorage<K extends Serializable, V, C extends Map<K, V>> implements Storage<K, V> {

    protected final ResourceLoadHandler loadHandle;

    protected final ResourceMeta<V> meta;
    protected C contain;
    protected List<V> sortData;

    public AbstractStorage(ResourceLoadHandler loadHandle, ResourceMeta<V> meta) {
        this.loadHandle = loadHandle;
        this.meta = meta;
    }

    /**
     * Index contain
     * <indexName, key, data>
     */
    protected Table<String, Serializable, List<V>> indexDataContain;

    protected Table<String, Serializable, V> uniqueIndexContain;

    @Override
    public V getUnique(String UniqueIndexName, Serializable value) {
        return uniqueIndexContain.get(UniqueIndexName, value);
    }

    @Override
    public List<V> getIndex(String indexName, Serializable value) {
        var list = indexDataContain.get(indexName, value);
        return Objects.isNull(list) ? Collections.emptyList() : list;
    }

    @Override
    public V getOrThrow(K k) {
        var value = get(k);
        if (Objects.nonNull(value)) {
            return value;
        }
        throw new NullPointerException(meta.getDataType().getSimpleName() + " no exist data with key is " + k);
    }

    @Override
    public void reload() {
        pushData(loadHandle.loadList(meta), meta);
    }

    @SuppressWarnings("unchecked")
    protected void pushData(List<V> dataList, ResourceMeta<V> meta) {
        Table<String, Serializable, List<V>> index = HashBasedTable.create();
        Table<String, Serializable, V> uniqueIndex = HashBasedTable.create();
        List<V> sortData = new ArrayList<>(dataList.size());
        C contain = newContain();
        for (V data : dataList) {
            contain.put((K) meta.getId().get(data), data);
            meta.forEachIndex((indexName, i) -> this.buildIndex(data, indexName, i, uniqueIndex, index));
            sortData.add(data);
            afterData(data);
        }
        if (Objects.nonNull(meta.getComparator())) {
            sortData.sort((Comparator<V>) BeanUtils.instantiateClass(meta.getComparator()));
        }
        this.contain = contain;
        this.sortData = Collections.unmodifiableList(sortData);
        this.indexDataContain = index;
        this.uniqueIndexContain = uniqueIndex;
    }

    protected void buildIndex(V data, String indexName, IndexGetter getter, Table<String, Serializable, V> uniqueIndexContain, Table<String, Serializable, List<V>> indexContain) {
        Serializable value = getter.get(data);
        if (Objects.isNull(value)) {
            throw new RuntimeException("@" + (GetterBuild.isIdGetter(getter) ? "Id" : "Index") + " value can't null. data: [" + JsonUtils.toJson(data) + "]");
        }
        if (getter.unique()) {
            if (uniqueIndexContain.contains(indexName, value)) {
                throw new IllegalArgumentException(MessageFormat.format("[{0} {1}] is not unique.", data.getClass().getName(), indexName));
            }
            uniqueIndexContain.put(indexName, value, data);
        } else {
            if (!indexContain.contains(indexName, value)) {
                indexContain.put(indexName, value, new ArrayList<>());
            }
            List<V> list = indexContain.get(indexName, value);
            Objects.requireNonNull(list).add(data);
        }
    }

    protected void afterData(Object data) {
        if (data instanceof ResourceDataLoaded loaded) {
            loaded.loaded();
        }
    }

    @Override
    public List<V> getAll() {
        return sortData;
    }

    protected abstract C newContain();

    public C getContain() {
        return contain;
    }
}
