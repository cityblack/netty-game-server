package com.lzh.game.business.grid;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DefaultGrid<T> implements Grid<T>, Serializable {

    @Serial
    private static final long serialVersionUID = -4317188699775774515L;

    protected static final int FULL_SIGN = -1;

    protected Object[] items;

    private int capacity;

    @Override
    public boolean indexHasItem(int index) {
        checkIndexAroundThrowOutOfBounds(index);
        return Objects.isNull(items[index]);
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
        int sum = 0;
        for (Object item : this.items) {
            if (Objects.isNull(item)) {
                sum += 1;
                if (sum >= num) {
                    return true;
                }
            }
        }
        return false;
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
        int firstEmpty = findFirstEmpty();
        if (firstEmpty == FULL_SIGN) {
            return FULL_SIGN;
        }
        this.setGrid(firstEmpty, item);
        return firstEmpty;
    }

    protected boolean checkIndexAround(int index) {
        return index > -1 && index < getCapacity();
    }

    protected void checkIndexAroundThrowOutOfBounds(int index) {
        if (!checkIndexAround(index)) {
            throw new ArrayIndexOutOfBoundsException(String.format("Grid size is %d and index is %d.", getCapacity(), index));
        }
    }

    @SuppressWarnings("unchecked")
    protected T getGrid(int index) {
        return (T) items[index];
    }

    protected void setGrid(int index, T item) {
        items[index] = item;
    }

    public int getCapacity() {
        return capacity;
    }

    @Override
    public void grow(int size) {
        var newList = new Object[this.capacity + size];
        System.arraycopy(this.items, 0, newList, 0, this.items.length);
        this.items = newList;
        this.capacity += size;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<Integer, T> loadedItems() {
        Map<Integer, T> items = new HashMap<>();
        for (int i = 0; i < this.items.length; i++) {
            if (Objects.nonNull(this.items[i])) {
                items.put(i, (T) this.items[i]);
            }
        }
        return items;
    }

    public DefaultGrid() {
        this(512);
    }

    public DefaultGrid(int tableSize) {
        this.items = new Object[tableSize];
        this.capacity = tableSize;
    }

    @SuppressWarnings("unchecked")
    private T remove(int index) {
        T item = (T) this.items[index];
        this.items[index] = null;
        return item;
    }

    protected int findFirstEmpty() {
        for (int i = 0; i < this.items.length; i++) {
            if (Objects.isNull(this.items[i])) {
                return i;
            }
        }
        return -1;
    }

    protected Object[] getItems() {
        return this.items;
    }

    protected void signEmptyGrid(int index) {
        this.items[index] = null;
    }
}
