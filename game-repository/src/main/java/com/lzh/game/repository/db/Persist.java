package com.lzh.game.repository.db;

public interface Persist {

    void put(Element element);

    void shutDown();
}
