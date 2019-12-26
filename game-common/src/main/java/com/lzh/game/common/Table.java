package com.lzh.game.common;

import java.util.Map;

/**
 *
 * @param <T>
 */
public interface Table<T> {

    int DEFAULT_TABLE_SIZE = 50;

    boolean indexHasItem(int index);

    T getItem(int index);

    T removeItem(int index);

    boolean isEnoughSize(int num);

    default boolean isEnoughSize() {
        return isEnoughSize(1);
    }

    /**
     * Add item to the first free space. Please call {@link #isEnoughSize()} before use this convent
     * @param item
     * @return Item in the grid index. if the value is -1 mean add fair
     */
    int addItem(T item);

    boolean putItem(int index, T item);
    /**
     * The table size
     * @return
     */
    int tableSize();
    /**
     * The number of store items
     * @return
     */
    int loadSize();

    /**
     *
     * @param size
     */
    void grow(int size);

    /**
     * Getting already loaded items mapping
     * @return <index, item>
     */
    Map<Integer, T> loadedItems();

}
