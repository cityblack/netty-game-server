package com.lzh.game.framework.rank;

import java.util.Objects;
import java.util.SortedSet;

/**
 * Rank表 由type和id组成唯一
 * 当排行种类不唯一的时候 id才会有意义(比如打多只boss的伤害排行)
 * @param <R>
 */
public class RankList<R extends RankInfo> {

    private int id;

    private RankType type;

    private SortedSet<R> rankInfos;

    private int max;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RankList<?> rankList = (RankList<?>) o;
        return id == rankList.id &&
                type == rankList.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type);
    }
}
