package com.lzh.game.start.model.item.bag.model;

import com.lzh.game.start.model.item.model.AbstractItem;
import lombok.Getter;

import java.util.Objects;

@Getter
public class RemoveItem {

    private AbstractItem item;

    private int num;

    private int sourceIndex;

    public static RemoveItem of(AbstractItem item, int num, int sourceIndex) {
        RemoveItem removeItem = new RemoveItem();
        removeItem.item = item;
        removeItem.num = num;
        removeItem.sourceIndex = sourceIndex;
        return removeItem;
    }

}
