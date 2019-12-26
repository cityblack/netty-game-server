package com.lzh.game.resource;

public interface ResourceReload {

    void reload(Class<?>[] classes);

    Class<?>[] classes();
}
