package com.lzh.game.framework.resource.data.cache;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.lzh.game.framework.resource.data.ResourceModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * In local memory cache
 */
public class MemoryResourceCacheFactory implements ResourceCacheFactory {

    @Override
    public <K extends Serializable, T> ResourceCache<K, T> newCache(Class<T> type, ResourceModel model) {
        return new Cache<>(type, model.getId().name());
    }

    protected class Cache<K extends Serializable, T> extends AbstractResourceCache<K, T>
            implements ResourceCache<K, T> {

        private Class<T> type;

        private String keyIndexName;

        public Cache(Class<T> type, String keyIndexName) {
            this.type = type;
            this.keyIndexName = keyIndexName;
        }

        /**
         * Index contain
         * <indexName, key, data>
         */
        private Table<String, Serializable, List<T>> indexDataContain;

        private Table<String, Serializable, T> uniqueIndexContain;

        private List<T> dataContain;

        @Override
        public List<T> findAll() {
            if (Objects.isNull(dataContain)) {
                return Collections.emptyList();
            }
            return dataContain;
        }

        @Override
        public List<T> findByIndex(String indexName, Serializable value) {
            if (Objects.isNull(indexDataContain)) {
                return Collections.emptyList();
            }
            List<T> list = indexDataContain.get(indexName, value);
            if (Objects.isNull(list)) {
                return Collections.emptyList();
            }
            return list;
        }

        @Override
        public T findById(K k) {
            return findOne(keyIndexName, k);
        }

        @Override
        public T findOne(String indexName, Serializable value) {
            if (Objects.isNull(uniqueIndexContain)) {
                return null;
            }
            return uniqueIndexContain.get(indexName, value);
        }

        @Override
        public void put(List<T> data, ResourceModel resourceModel, Consumer<T> beforePut) {
            Table<String, Serializable, List<T>> index = HashBasedTable.create();
            Table<String, Serializable, T> uniqueIndex = HashBasedTable.create();
            List<T> contain = new ArrayList<>(data.size());
            for (T d : data) {
                if (Objects.nonNull(beforePut)) {
                    beforePut.accept(d);
                }
                contain.add(d);

                resourceModel.forEachIndex((indexName, i) -> this.buildIndex(d, indexName, i, uniqueIndex, index));
            }
            this.indexDataContain = index;
            this.uniqueIndexContain = uniqueIndex;
            this.dataContain = contain;
        }

        @Override
        public void clear() {
            this.indexDataContain = null;
            this.uniqueIndexContain = null;
            this.dataContain = null;
        }
    }
}
