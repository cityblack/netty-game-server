package com.lzh.game.business.grid;

import java.util.Map;

/**
 * Mark grid change.
 * @param <T>
 */
public interface MarkGrid<T> extends Grid<T> {

    void clearMark();

    Map<Integer, T> getChange();
}
