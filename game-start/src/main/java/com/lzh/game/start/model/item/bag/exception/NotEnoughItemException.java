package com.lzh.game.start.model.item.bag.exception;

public class NotEnoughItemException extends Exception {

    public NotEnoughItemException(int itemModel, int num) {
        super("Not enough item to reduce. itemModel:" + itemModel + " num:" + num);
    }

    public NotEnoughItemException(long itemId, int num) {
        super("Not enough item to reduce. itemId:" + itemId + " num:" + num);
    }
}
