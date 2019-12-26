package com.lzh.game.resource.data;

@FunctionalInterface
public interface ResourceNameStrategyStandard {

    String toTableName(String name);

}
