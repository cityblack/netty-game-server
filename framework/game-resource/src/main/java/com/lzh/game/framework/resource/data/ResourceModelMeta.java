package com.lzh.game.framework.resource.data;

import java.util.stream.Stream;

/**
 * @Resource manage
 */
public interface ResourceModelMeta extends Iterable<ResourceModel> {

    ResourceModel getResource(String resourceName);

    ResourceModel getResource(Class<?> resourceType);

    Stream<String> getAllResourceName();

    Stream<Class<?>> getAllResourceType();
}
