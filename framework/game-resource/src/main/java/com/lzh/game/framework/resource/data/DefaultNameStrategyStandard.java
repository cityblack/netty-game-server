package com.lzh.game.framework.resource.data;

public class DefaultNameStrategyStandard implements ResourceNameStrategyStandard {

    @Override
    public String toTableName(String name) {
        return name.substring(0, 1).toLowerCase() + name.substring(1);
    }
}
