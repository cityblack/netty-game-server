package com.lzh.game.resource.data;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.lzh.game.common.serialization.JsonUtils;
import com.lzh.game.resource.ResourceLoaded;
import com.lzh.game.resource.reload.ResourceReloadMange;
import com.lzh.game.resource.uitl.SortedArrayList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.lang.NonNull;

import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public abstract class DefaultResourceManageHandler implements ResourceManageHandler {

    public abstract <T>List<T> getData(Class<T> type, String resourceName);

    private Table<Class<?> ,String, Map<Object, List<?>>> indexDataContain = HashBasedTable.create();

    private Table<Class<?> ,String, Map<Object, Object>> uniqueIndexContain = HashBasedTable.create();

    private Map<Class<?>, List<?>> dataContain = new HashMap<>();

    private ResourceModelFactory factory;

    private ResourceReloadMange reloadMange;

    public DefaultResourceManageHandler(ResourceReloadMange reloadMange, ResourceModelFactory factory) {
        this.reloadMange = reloadMange;
        this.factory = factory;
    }

    @Override
    public <T> List<T> findAll(@NonNull Class<T> clazz) {

        return listNull(dataContain.get(clazz));
    }

    @Override
    public <T> List<T> findByIndex(@NonNull Class<T> clazz, @NonNull String index, @NonNull Object value) {

        Map<Object, List<?>> contain = indexDataContain.get(clazz, index);
        if (Objects.isNull(contain)) {
            return Collections.EMPTY_LIST;
        }
        return listNull(contain.get(value));
    }

    private <T>List<T> listNull(Object list) {
        return Objects.isNull(list) ? Collections.EMPTY_LIST : (List<T>) list;
    }

    @Override
    public <T> T findOne(@NonNull Class<T> clazz, @NonNull String uniqueIndex, @NonNull Object value) {

        return valueNull(uniqueIndexContain.get(clazz, uniqueIndex).get(value));
    }

    @Override
    public <T, K> T findById(@NonNull Class<T> clazz, K k) {
        String index = getResourceModelManage().getResource(clazz).getId().name();

        Map<Object, Object> contain = uniqueIndexContain.get(clazz, index);
        if (Objects.isNull(contain)) {
            return null;
        }
        return valueNull(contain.get(k));
    }

    private <T>T valueNull(Object o) {
        return Objects.isNull(o) ? null : (T) o;
    }

    @Override
    public void reload() {
        clear();
        getResourceModelManage().forEach(e -> flashData(e.getDataType()));
        Class<?>[] clazz = getResourceModelManage().getAllResourceType().toArray(Class<?>[]::new);
        reloadMange.getAllReload().forEach(e -> e.reload(clazz));
    }

    @Override
    public void reload(Class<?>[] clazz) {
        // 先全部加载完成 再执行reload方法 防止reload实现有互相依赖关系出错中断流程
        Stream.of(clazz).peek(this::reload)
                .flatMap(c -> reloadMange.getReload(c).stream())
                .collect(Collectors.toList())
                .forEach(e -> e.reload(clazz));
    }

    protected void reload(Class<?> clazz) {
        clear(clazz);
        flashData(clazz);
    }

    private void flashData(Class<?> type) {
        ResourceModel model = getResourceModelManage().getResource(type);
        List<?> list = getData(type, model.getResourceName());
        list.forEach(e -> put(e, model));
    }

    protected <V>void put(V data, ResourceModel resourceModel) {
        resourceModel.forEachIndex((k,v) ->  buildIndex(data, v));
        List<V> contain = getDataContain(resourceModel.getDataType(), resourceModel.getComparator());
        contain.add(data);
        afterPutData(data);
    }

    protected void afterPutData(Object data) {
        if (data instanceof ResourceLoaded) {
            ((ResourceLoaded) data).loaded();
        }
    }

    protected void clear() {
        indexDataContain.clear();
        uniqueIndexContain.clear();
        dataContain.clear();
    }

    protected void clear(Class<?> key) {
        indexDataContain.row(key).clear();
        uniqueIndexContain.row(key).clear();
        dataContain.remove(key);
    }

    protected List getDataContain(Class<?> type, Class<? extends Comparator> comparator) {
        List<?> list = dataContain.get(type);
        if (Objects.isNull(list)) {
            list = new SortedArrayList<>(BeanUtils.instantiateClass(comparator));
            dataContain.put(type, list);
        }
        return list;
    }

    private <T>void buildIndex(T data, IndexGetter getter) {

        Class<?> type = data.getClass();
        String indexName = getter.name();
        Object value = getter.get(data);

        if (Objects.isNull(value)) {

            throw new RuntimeException("@" + (GetterBuild.isIdGetter(getter) ? "Id" : "Index") + " value can't null. data: [" + JsonUtils.toJson(data) +"]");
        }
        if (getter.unique()) {
            Map<Object, Object> contain = getUniqueIndexContain(type, indexName);
            if (contain.containsKey(value)) {
                throw new IllegalArgumentException(MessageFormat.format("[{0} {1}] is not unique.",type.getName(), indexName));
            }
            contain.put(value, data);
        } else {
            Map<Object, List<?>> contain = getIndexContain(type, indexName);
            List<Object> list = (List<Object>) contain.getOrDefault(value, new SortedArrayList<>(getter.comparator()));
            list.add(data);
            contain.putIfAbsent(value, list);
        }

    }

    private Map<Object, Object> getUniqueIndexContain(Class<?> type, String indexName) {
        if (uniqueIndexContain.contains(type, indexName)) {
            return uniqueIndexContain.get(type, indexName);
        } else {
            Map<Object, Object> map = new HashMap<>();
            uniqueIndexContain.put(type, indexName, map);
            return map;
        }
    }

    private Map<Object, List<?>> getIndexContain(Class<?> type, String indexName) {

        if (indexDataContain.contains(type, indexName)) {
            return indexDataContain.get(type, indexName);
        } else {
            Map<Object, List<?>> map = new HashMap<>();
            indexDataContain.put(type, indexName, map);
            return map;
        }
    }

    public ResourceReloadMange getReloadMange() {
        return reloadMange;
    }

    public ResourceModelFactory getResourceModelManage() {
        return factory;
    }
}
