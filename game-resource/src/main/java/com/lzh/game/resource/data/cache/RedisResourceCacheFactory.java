package com.lzh.game.resource.data.cache;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.lzh.game.common.serialization.JsonUtils;
import com.lzh.game.resource.data.GetterBuild;
import com.lzh.game.resource.data.IndexGetter;
import com.lzh.game.resource.data.ResourceModel;
import org.redisson.Redisson;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.*;
import java.util.function.Consumer;

public class RedisResourceCacheFactory implements ResourceCacheFactory {

    private Redisson redisson;

    public RedisResourceCacheFactory(Redisson redisson) {
        this.redisson = redisson;
    }

    @Override
    public <K extends Serializable, T> ResourceCache<K, T> newCache(Class<T> type, ResourceModel model) {
        return new RedisCache<>(model.getResourceName());
    }

    protected class RedisCache<K extends Serializable, T> extends AbstractResourceCache<K, T>
            implements ResourceCache<K, T> {

        private String resourceName;
        // IndexName map. reduce string malloc
        private Map<String, String> indexMapping;

        private String resourceKey;

        private String uniqueIndexKeyName;

        public RedisCache(String resourceName) {
            this.resourceName = resourceName;
            this.indexMapping = new HashMap<>();
            this.resourceKey = resourceName + ":resource:$key";
            this.uniqueIndexKeyName = resourceName + ":unique:$index:$name";
        }

        @Override
        public List<T> findAll() {
            return redisson.getList(resourceName);
        }

        @Override
        public List<T> findByIndex(String indexName, Serializable value) {
            return (List<T>) redisson.getMap(getOrPutIndex(indexName)).get(value);
        }

        @Override
        public T findById(K k) {
            return findOne(this.resourceKey, k);
        }

        @Override
        public T findOne(String indexName, Serializable value) {
            return (T) redisson.getMap(this.uniqueIndexKeyName).get(value);
        }

        @Override
        public void put(List<T> data, ResourceModel resourceModel, Consumer<T> beforePut) {
            Table<String, Serializable, List<T>> index = HashBasedTable.create();
            Table<String, Serializable, T> uniqueIndex = HashBasedTable.create();
//            List<T> contain = new ArrayList<>(data.size());
//            Map<Serializable, T> contain = new HashMap<>();
            for (T d : data) {
                if (Objects.nonNull(beforePut)) {
                    beforePut.accept(d);
                }
//                contain.add(d);
                resourceModel.forEachIndex((indexName, i) -> this.buildIndex(d, indexName, i, uniqueIndex, index));
            }

            clear();
//            redisson.getList(resourceName).addAll(contain);
            for (String indexName : index.rowKeySet()) {
                Map<Serializable, List<T>> map = index.row(indexName);
                for (Map.Entry<Serializable, List<T>> entry : map.entrySet()) {
                    redisson.getMap(getOrPutIndex(indexName)).put(entry.getKey(), entry.getValue());
                }
            }

            for (String indexName : uniqueIndex.rowKeySet()) {
                Map<Serializable, T> map = uniqueIndex.row(indexName);
                for (Map.Entry<Serializable, T> entry : map.entrySet()) {
                    redisson.getMap(getOrPutIndex(indexName)).put(entry.getKey(), entry.getValue());
                }
            }
        }

        @Override
        public void clear() {
            redisson.getKeys().delete(this.resourceName + "*");
        }

        protected String getOrPutIndex(String indexName) {
            String index = this.indexMapping.get(indexName);
            if (Objects.isNull(index)) {
                index = this.resourceName + ":" +  indexName;
                synchronized (this.indexMapping) {
                    if (!this.indexMapping.containsKey(indexName)) {
                        this.indexMapping.put(indexName, index);
                    }
                }
            }
            return index;
        }
    }
}
