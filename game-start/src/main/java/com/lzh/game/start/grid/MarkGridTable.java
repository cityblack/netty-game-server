package com.lzh.game.start.grid;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

public class MarkGridTable<T> extends GridTable<T> implements MarkTable<T> {

    private transient BitSet mark = new BitSet();

    protected MarkGridTable(int size) {
        super(size);
    }

    @Deprecated
    public MarkGridTable() {

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
        markChangeIndex(index);
        return item;
    }

    @Override
    public int addItem(T item) {
        int index = super.addItem(item);
        markIndex(index != -1, index);
        return index;
    }

    @Override
    public void grow(int size) {
        int oldSize = this.tableSize();
        super.grow(size);
        mark.set(oldSize - 1, this.tableSize() - 1);
    }

    private void markChangeIndex(int index) {
        mark.set(index);
    }

    private void markIndex(boolean needMark, int index) {
        if (needMark) {
            markChangeIndex(index);
        }
    }

    @Override
    public void clearMark() {
        mark.clear();
    }

    @Override
    public Map<Integer, T> getChange() {
        Map<Integer, T> change = new HashMap<>();

        for (int i = 0; i < this.tableSize(); i++) {
            if (mark.get(i)) {
                change.put(i, this.getItem(i));
            }
        }
        return change;
    }

    protected BitSet getMark() {
        return mark;
    }
}
