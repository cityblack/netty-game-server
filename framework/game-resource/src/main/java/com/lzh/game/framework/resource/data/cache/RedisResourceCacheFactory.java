package com.lzh.game.framework.resource.data.cache;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.lzh.game.framework.resource.data.ResourceMeta;
import org.redisson.Redisson;

import java.io.Serializable;
import java.util.*;
import java.util.function.Consumer;

public class RedisResourceCacheFactory implements ResourceCacheFactory {

    private Redisson redisson;

    public RedisResourceCacheFactory(Redisson redisson) {
        this.redisson = redisson;
    }

    @Override
    public <K extends Serializable, T> ResourceCache<K, T> newCache(Class<T> type, ResourceMeta model) {
        return new RedisCache<>(model.getResourceName(), model.getId().name());
    }

    /**
     * Use map contain
     * @param <K>
     * @param <T>
     */
    protected class RedisCache<K extends Serializable, T> extends AbstractResourceCache<K, T>
            implements ResourceCache<K, T> {

        private String resourceName;
        // IndexName map. reduce string malloc
        private Map<String, String> indexMapping;

        private String keyIndex;

        public RedisCache(String resourceName, String keyIndexName) {
            this.resourceName = resourceName;
            this.indexMapping = new HashMap<>();
            this.keyIndex = keyIndexName;
        }

        /**
         * Use map collection so that performance is a big problem.
         * Not used unless necessary
         * @return
         */
        @Override
        public List<T> findAll() {
            Collection<Object> contain = redisson.getMap(getOrPutIndex(this.keyIndex)).values();
            List<T> list = new ArrayList<>(contain.size());
            for (Object o : contain) {
                list.add((T) o);
            }
            return list;
        }

        // Map<indexName, Map<K, List<T>>
        @Override
        public List<T> findByIndex(String indexName, Serializable value) {
            return (List<T>) redisson.getMap(getOrPutIndex(indexName)).get(value);
        }

        @Override
        public T findById(K k) {
            return findOne(this.keyIndex, k);
        }

        // Map<indexName, Map<K, T>>
        @Override
        public T findOne(String indexName, Serializable value) {
            return (T) redisson.getMap(getOrPutIndex(indexName)).get(value);
        }

        @Override
        public void put(List<T> data, ResourceMeta resourceModel, Consumer<T> beforePut) {
            Table<String, Serializable, List<T>> index = HashBasedTable.create();
            Table<String, Serializable, T> uniqueIndex = HashBasedTable.create();
            for (T d : data) {
                if (Objects.nonNull(beforePut)) {
                    beforePut.accept(d);
                }
                resourceModel.forEachIndex((indexName, i) -> this.buildIndex(d, indexName, i, uniqueIndex, index));
            }

            clear();
            // index data to redis
            for (String indexName : index.rowKeySet()) {
                Map<Serializable, List<T>> map = index.row(indexName);
                for (Map.Entry<Serializable, List<T>> entry : map.entrySet()) {
                    redisson.getMap(getOrPutIndex(indexName)).put(entry.getKey(), entry.getValue());
                }
            }
            // unique index data to redis
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
