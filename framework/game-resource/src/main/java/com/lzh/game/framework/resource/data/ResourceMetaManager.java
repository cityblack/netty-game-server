package com.lzh.game.framework.resource.data;

import java.util.stream.Stream;

/**
 * @Resource manage
 */
public interface ResourceMetaManager extends Iterable<ResourceMeta> {

    ResourceMeta getResource(String resourceName);

    ResourceMeta getResource(Class<?> resourceType);

    Stream<String> getAllResourceName();

    Stream<Class<?>> getAllResourceType();
}
