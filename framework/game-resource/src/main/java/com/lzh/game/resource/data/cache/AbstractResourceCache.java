package com.lzh.game.resource.data.cache;

import com.google.common.collect.Table;
import com.lzh.game.common.serialization.JsonUtils;
import com.lzh.game.resource.data.GetterBuild;
import com.lzh.game.resource.data.IndexGetter;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class AbstractResourceCache<K extends Serializable, T>
        implements ResourceCache<K, T> {

    protected void buildIndex(T data, String indexName, IndexGetter getter, Table<String, Serializable, T> uniqueIndexContain, Table<String, Serializable, List<T>> indexContain) {
        Serializable value = getter.get(data);
        if (Objects.isNull(value)) {
            throw new RuntimeException("@" + (GetterBuild.isIdGetter(getter) ? "Id" : "Index") + " value can't null. data: [" + JsonUtils.toJson(data) +"]");
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
            List<T> list = indexContain.get(indexName, value);
            list.add(data);
        }
    }
}
