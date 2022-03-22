package com.lzh.game.resource;

import com.lzh.game.common.serialization.JsonUtils;

@Resource
public class ConfigValueResource<T> implements StorageInstance<T> {

    @Id
    private String id;

    private String value;

    @Override
    public T getValue() {
        // remove {}
        String content = value.substring(0, value.length() - 1);
        return (T) JsonUtils.toArray(content, Integer.class);
    }
}
