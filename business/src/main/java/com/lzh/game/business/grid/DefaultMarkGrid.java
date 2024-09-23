package com.lzh.game.business.grid;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

public class DefaultMarkGrid<T> extends DefaultGrid<T> implements MarkGrid<T> {

    private final transient BitSet mark = new BitSet();

    protected DefaultMarkGrid(int size) {
        super(size);
    }

    @Deprecated
    public DefaultMarkGrid() {

    }

    @Override
    public boolean putItem(int index, T item) {
        boolean b = super.putItem(index, item);
        markIndex(b, index);
        return b;
    }

    @Override
    public T removeItem(int index) {
        T item = super.removeItem(index);
        mark(index);
        return item;
    }

    @Override
    public int addItem(T item) {
        int index = super.addItem(item);
        markIndex(index != FULL_SIGN, index);
        return index;
    }

    @Override
    public void grow(int size) {
        int oldSize = this.getCapacity();
        super.grow(size);
        mark.set(oldSize - 1, this.getCapacity() - 1);
    }

    private void markIndex(boolean needMark, int index) {
        if (needMark) {
            mark(index);
        }
    }

    @Override
    public void clearMark() {
        mark.clear();
    }

    @Override
    public Map<Integer, T> getChange() {
        Map<Integer, T> change = new HashMap<>();

        for (int i = 0; i < this.getCapacity(); i++) {
            if (mark.get(i)) {
                change.put(i, this.getItem(i));
            }
        }
        return change;
    }

    protected BitSet getMark() {
        return mark;
    }

    @Override
    public void mark(int index) {
        this.mark.set(index);
    }

    @Override
    protected void signEmptyGrid(int index) {
        super.signEmptyGrid(index);
        mark(index);
    }

    @Override
    protected void setGrid(int index, T item) {
        super.setGrid(index, item);
        mark(index);
    }
}
