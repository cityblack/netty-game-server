package com.lzh.game.framework.resource.data.load;

import com.lzh.game.framework.resource.data.meta.ResourceMeta;

import java.util.List;

/**
 * Resource load handle
 * Default use mongo to load resource. Rewrite the interface If wanna load from file(csv, excel) or complex(net/file/db. by class type)
 */
public interface ResourceLoadHandler {

    <T>List<T> loadList(ResourceMeta<T> meta);
}
