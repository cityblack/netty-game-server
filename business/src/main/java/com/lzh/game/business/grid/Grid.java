package com.lzh.game.business.grid;

import java.util.Map;

/**
 * @author zehong.l
 * @since 2024-09-23 16:46
 **/
public interface Grid<T> {

    boolean indexHasItem(int index);

    T getItem(int index);

    T removeItem(int index);

    boolean isEnoughSize(int num);

    default boolean isEnoughSize() {
        return isEnoughSize(1);
    }

    boolean putItem(int index, T item);

    int addItem(T item);

    void grow(int size);

    /**
     * get all items
     * @return <index , Item>
     */
    Map<Integer, T> loadedItems();
}
