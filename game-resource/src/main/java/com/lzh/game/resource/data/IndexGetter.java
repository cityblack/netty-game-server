package com.lzh.game.resource.data;


import java.util.Comparator;

public interface IndexGetter<C> extends Getter {

    Comparator<C> comparator();

    boolean unique();
}
