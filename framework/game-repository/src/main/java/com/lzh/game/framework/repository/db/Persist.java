package com.lzh.game.framework.repository.db;

public interface Persist {

    void put(Element element);

    void shutDown();
}
