package com.lzh.game.framework.resource;

public interface ResourceReload {

    void reload(Class<?>[] classes);

    Class<?>[] classes();
}
