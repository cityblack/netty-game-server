package com.lzh.game.start.grid;

import com.lzh.game.common.Table;

import java.util.Map;

public interface MarkTable<T> extends Table<T> {

    void clearMark();

    Map<Integer, T> getChange();
}
