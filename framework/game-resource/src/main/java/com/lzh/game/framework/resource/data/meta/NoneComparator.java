package com.lzh.game.framework.resource.data.meta;

import java.util.Comparator;

/**
 * @author zehong.l
 * @since 2024-10-14 17:50
 **/
public class NoneComparator implements Comparator<Void> {

    @Override
    public int compare(Void o1, Void o2) {
        return 0;
    }
}
