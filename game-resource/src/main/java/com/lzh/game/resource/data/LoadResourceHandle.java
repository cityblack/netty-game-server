package com.lzh.game.resource.data;

import java.util.List;

/**
 * Resource load handle
 * Default use mongo to load resource. Rewrite the interface If wanna load from file(csv, excel) or complex(net/file/db. by class type)
 */
public interface LoadResourceHandle {

    List<?> loadList(Class<?> type, String resourceName);
}
