package com.lzh.game.start.model.item.bag.service;

import com.lzh.game.start.model.player.Player;

import java.util.Map;

public interface PlayerBagService extends ItemVerify, ItemCurd {

    @Override
    default boolean isEnoughGrid(Player player, int itemModel, int num) {

        int len = itemNeedGridLen(player, itemModel, num);
        if (len == 0) {
            return true;
        }
        return hasMoreGrid(player, len);
    }

    @Override
    default boolean isEnoughGrid(Player player, Map<Integer, Integer> itemAndNum) {
        int len = itemNeedGridLen(player, itemAndNum);
        if (len <= 0) {
            return true;
        }
        return hasMoreGrid(player, len);
    }

    void update(Player player);
}
