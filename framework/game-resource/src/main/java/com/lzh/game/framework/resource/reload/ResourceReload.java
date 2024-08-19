package com.lzh.game.framework.resource.reload;

public interface ResourceReload {

    void reload(Class<?>[] classes);

    Class<?>[] classes();
}
