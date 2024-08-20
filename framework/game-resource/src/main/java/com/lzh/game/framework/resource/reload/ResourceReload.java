package com.lzh.game.framework.resource.reload;

import java.util.List;

public interface ResourceReload {

    void reload(List<Class<?>> classes);

    Class<?>[] classes();
}
