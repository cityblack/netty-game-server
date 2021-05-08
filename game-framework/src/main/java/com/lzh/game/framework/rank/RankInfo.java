package com.lzh.game.framework.rank;

import lombok.Data;

import java.io.Serializable;

@Data
public abstract class RankInfo<K extends Serializable> implements Comparable<RankInfo> {

    private K key;

    private long updateTime;

    private long score;

}
