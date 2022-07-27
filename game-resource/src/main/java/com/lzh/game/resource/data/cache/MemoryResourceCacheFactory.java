package com.lzh.game.resource.data.cache;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.lzh.game.common.serialization.JsonUtils;
import com.lzh.game.resource.data.GetterBuild;
import com.lzh.game.resource.data.IndexGetter;
import com.lzh.game.resource.data.ResourceModel;

import java.io.Serializable;
import java.text.MessageFormat;
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
      public void put(List<T> data, ResourceModel resourceModel, Consumer<T> beforePut) {
         Table<String, Serializable, List<T>> index = HashBasedTable.create();
         Table<String, Serializable, T> uniqueIndex = HashBasedTable.create();
         List<T> contain = new ArrayList<>();
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
         /*this.indexDataContain.clear();
         this.uniqueIndexContain.clear();
         if (Objects.nonNull(this.dataContain)) {
            this.dataContain.clear();
         }*/
      }

      private void buildIndex(T data, String indexName, IndexGetter getter, Table<String, Serializable, T> uniqueIndexContain, Table<String, Serializable, List<T>> indexContain) {

         Serializable value = getter.get(data);
         if (Objects.isNull(value)) {
            throw new RuntimeException("@" + (GetterBuild.isIdGetter(getter) ? "Id" : "Index") + " value can't null. data: [" + JsonUtils.toJson(data) +"]");
         }
         if (getter.unique()) {
            if (uniqueIndexContain.contains(indexName, value)) {
               throw new IllegalArgumentException(MessageFormat.format("[{0} {1}] is not unique.",type.getName(), indexName));
            }
            uniqueIndexContain.put(indexName, value, data);
         } else {
            if (indexContain.contains(indexName, value)) {
               indexContain.put(indexName, value, new ArrayList<>());
            }
            List<T> list = indexContain.get(indexName, value);
            list.add(data);
         }
      }
   }
}
