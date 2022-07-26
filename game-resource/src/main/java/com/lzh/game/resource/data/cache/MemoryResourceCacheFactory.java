package com.lzh.game.resource.data.cache;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.lzh.game.common.serialization.JsonUtils;
import com.lzh.game.resource.data.GetterBuild;
import com.lzh.game.resource.data.IndexGetter;
import com.lzh.game.resource.data.ResourceModel;
import com.lzh.game.resource.uitl.SortedArrayList;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * In local memory cache
 */
public class MemoryResourceCacheFactory implements ResourceCacheFactory {

   @Override
   public <K extends Serializable, T> ResourceCache<K, T> newCache(Class<T> type, ResourceModel model) {
      return new Cache<>(type, model.getId().name());
   }

   protected class Cache<K extends Serializable, T> implements ResourceCache<K, T> {

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
      private Table<String, Serializable, List<T>> indexDataContain = HashBasedTable.create();

      private Table<String, Serializable, T> uniqueIndexContain = HashBasedTable.create();

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
         return uniqueIndexContain.get(indexName, value);
      }

      @Override
      public void put(T data, ResourceModel model) {
         if (Objects.isNull(dataContain)) {
            this.dataContain = new SortedArrayList<>(BeanUtils.instantiateClass(model.getComparator()));
         }
         this.dataContain.add(data);
         model.forEachIndex((indexName, index) -> this.buildIndex(data, indexName, index));
      }

      @Override
      public void clear() {
         this.indexDataContain.clear();
         this.uniqueIndexContain.clear();
         if (Objects.nonNull(this.dataContain)) {
            this.dataContain.clear();
         }
      }

      private void buildIndex(T data, String indexName, IndexGetter<T> getter) {

         Serializable value = getter.get(data);
         if (Objects.isNull(value)) {
            throw new RuntimeException("@" + (GetterBuild.isIdGetter(getter) ? "Id" : "Index") + " value can't null. data: [" + JsonUtils.toJson(data) +"]");
         }
         if (getter.unique()) {
            if (this.uniqueIndexContain.contains(indexName, value)) {
               throw new IllegalArgumentException(MessageFormat.format("[{0} {1}] is not unique.",type.getName(), indexName));
            }
            this.uniqueIndexContain.put(indexName, value, data);
         } else {
            if (this.indexDataContain.contains(indexName, value)) {
               this.indexDataContain.put(indexName, value, new SortedArrayList<>(getter.comparator()));
            }
            List<T> list = this.indexDataContain.get(indexName, value);
            list.add(data);
         }
      }
   }
}
