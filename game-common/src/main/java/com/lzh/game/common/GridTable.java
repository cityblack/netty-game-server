package com.lzh.game.common;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.*;
import java.util.stream.IntStream;

public class GridTable<T> implements Table<T>, Serializable {

    private static final long serialVersionUID = -4317188699775774515L;
    // Already put items
    private Map<Integer, T> items;
    // Table current size
    private int size;
    // Empty grid queue
    private transient PriorityQueue<Integer> emptyIndex;
    /**
     * Delay reset {@link #emptyIndex} sign
     */
    private transient boolean initEmptyIndex;

    @Override
    public boolean indexHasItem(int index) {
        checkIndexAroundThrowOutOfBounds(index);
        return this.items.containsKey(index);
    }

    @Override
    public T getItem(int index) {
        checkIndexAroundThrowOutOfBounds(index);
        return this.getGrid(index);
    }

    @Override
    public T removeItem(int index) {
        checkIndexAroundThrowOutOfBounds(index);

        return remove(index);
    }

    @JsonIgnore
    @Override
    public boolean isEnoughSize(int num) {

        return this.getEmptyIndex().size() >= num;
    }

    @JsonIgnore
    @Override
    public boolean isEnoughSize() {
        return isEnoughSize(1);
    }

    @Override
    public boolean putItem(int index, T item) {
        checkIndexAroundThrowOutOfBounds(index);
        if (indexHasItem(index)) {
            return false;
        }
        setGrid(index, item);
        return true;
    }

    @Override
    public int addItem(T item) {
        if (!isEnoughSize(1)) {
            return -1;
        }
        int firstEmpty = getEmptyIndex().peek();
        this.setGrid(firstEmpty, item);
        return firstEmpty;
    }

    protected boolean checkIndexAround(int index) {
        return index > -1 && index < tableSize();
    }

    protected void checkIndexAroundThrowOutOfBounds(int index) {
        if (!checkIndexAround(index)) {
            throw new ArrayIndexOutOfBoundsException(String.format("Table size is %d and index is %d.", tableSize(), index));
        }
    }

    protected T getGrid(int index) {
        return this.items.get(index);
    }

    protected void setGrid(int index, T item) {
        this.items.put(index, item);
        this.getEmptyIndex().remove(index);
    }

    public int tableSize() {
        return size;
    }

    @Override
    public int loadSize() {
        return this.items.size();
    }

    @Override
    public void grow(int size) {
        IntStream.rangeClosed(this.size, this.size + size).forEach(e -> this.getEmptyIndex().add(e - 1));
        this.size += size;
    }

    @Override
    public Map<Integer, T> loadedItems() {

        return new HashMap<>(this.items);
    }

    public GridTable() {
        this(DEFAULT_TABLE_SIZE);
    }

    public GridTable(int tableSize) {
        this.items = new HashMap<>(tableSize);
        this.size = tableSize;
        this.emptyIndex = new PriorityQueue<>();
    }

    protected void initEmpty() {
        IntStream.range(0, this.size).filter(e -> !items.containsKey(e)).forEach(getEmptyIndex()::add);
    }

    private T remove(int index) {
        T item = this.items.remove(index);
        if (Objects.nonNull(item)) {
            addEmptyIndex(index);
        }
        return item;
    }

    private void addEmptyIndex(int index) {
        if (!this.getEmptyIndex().contains(index)) {
            this.getEmptyIndex().add(index);
        }
    }

    protected Map<Integer, T> getItems() {
        return items;
    }

    /**
     * Delay set empty index
     * @return
     */
    protected PriorityQueue<Integer> getEmptyIndex() {
        if (!initEmptyIndex) {
            initEmptyIndex = true;
            initEmpty();
        }
        return this.emptyIndex;
    }
}
