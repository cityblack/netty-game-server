package com.lzh.game.framework.common.utils;

public final class Pair<First, Second> {

    private First first;
    private Second second;

    public static <T1, T2> Pair<T1, T2> of(T1 first, T2 second) {
        return new Pair<>(first, second);
    }

    public Pair() {
    }

    private Pair(First first, Second second) {
        this.first = first;
        this.second = second;
    }

    public First getFirst() {
        return first;
    }

    public void setFirst(First first) {
        this.first = first;
    }

    public Second getSecond() {
        return second;
    }

    public void setSecond(Second second) {
        this.second = second;
    }

    @Override
    public String toString() {
        return "[" + first + ", " + second + ']';
    }
}
