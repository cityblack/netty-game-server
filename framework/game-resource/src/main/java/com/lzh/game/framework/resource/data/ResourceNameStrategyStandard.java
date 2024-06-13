package com.lzh.game.framework.resource.data;

@FunctionalInterface
public interface ResourceNameStrategyStandard {

    String toTableName(String name);

}
