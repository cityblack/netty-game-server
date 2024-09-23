package com.lzh.game.framework.rookie.model;

import lombok.Data;

/**
 * @author zehong.l
 * @since 2024-09-20 16:12
 **/
@Data
public abstract class AbstractItem {

    protected long guid = -1000;
    protected int index = 10;
}