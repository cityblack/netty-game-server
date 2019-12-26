package com.lzh.game.resource.data;

public interface ResourceModelFactory extends Iterable<ResourceModel> {

    ResourceModel getResource(String resourceName);

    ResourceModel getResource(Class<?> resourceType);

}
