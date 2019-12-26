package com.lzh.game.common;

import java.util.Map;

public interface MarkTable<T> extends Table<T> {

    void clearMark();

    Map<Integer, T> getChange();
}
