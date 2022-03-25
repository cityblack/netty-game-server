package com.lzh.game.resource.data;

import java.util.stream.Stream;

/**
 * @Resource manage
 */
public interface ResourceModelFactory extends Iterable<ResourceModel> {

    ResourceModel getResource(String resourceName);

    ResourceModel getResource(Class<?> resourceType);

    Stream<String> getAllResourceName();

    Stream<Class<?>> getAllResourceType();
}
