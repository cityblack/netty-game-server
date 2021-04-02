package com.lzh.game.start.model.item.bag.service;

import com.lzh.game.start.model.item.bag.exception.NotEnoughItemException;
import com.lzh.game.start.model.item.bag.model.RemoveItem;
import com.lzh.game.start.log.LogReason;
import com.lzh.game.start.model.item.model.AbstractItem;
import com.lzh.game.start.model.player.Player;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public interface ItemCurd {

    /**
     * 根据物品resource id消耗
     * @param player
     * @param itemModel
     * @param num
     * @return
     */
    List<RemoveItem> reduceItems(Player player, int itemModel, int num, LogReason logReason) throws NotEnoughItemException;

    /**
     * 根据物品唯一id 消耗
     * @param player
     * @param itemId
     * @param num
     * @return
     */
    RemoveItem reduceItemById(Player player, long itemId, int num, LogReason logReason) throws NotEnoughItemException;

    /**
     * 根据物品唯一id移除
     * @param itemId
     * @return
     */
    RemoveItem removeItem(Player player, long itemId, LogReason logReason) throws NotEnoughItemException;

    /**
     * 物品需要的长度
     * @param items
     * @return
     */
    int itemNeedGridLen(Player player, List<AbstractItem> items);
    /**
     * 物品需要的长度
     * @param itemModel
     * @param num
     * @return
     */
    int itemNeedGridLen(Player player, int itemModel, int num);

    /**
     * 物品需要的格子长度
     * @param player
     * @param itemAndNum 物品id和数量
     * @return
     */
    default int itemNeedGridLen(Player player, Map<Integer, Integer> itemAndNum) {
        int len = 0;
        for (Map.Entry<Integer, Integer> entry: itemAndNum.entrySet()) {
            len += itemNeedGridLen(player, entry.getKey(), entry.getValue());
        }
        return len;
    }

    /**
     * 添加物品
     * @param abstractItems
     * @return
     */
    void addItem(Player player, List<AbstractItem> abstractItems, LogReason logReason);

    /**
     * 添加物品
     * @param item
     * @return
     */
    default void addItem(Player player, AbstractItem item, LogReason logReason) {
        addItem(player, Arrays.asList(item), logReason);
    }

    /**
     *
     * @param player
     * @param itemId
     * @return
     */
    List<AbstractItem> getItemsByItemModelId(Player player, int itemId);

    /**
     *
     * @param player
     * @param object
     * @return
     */
    AbstractItem getItemByObjectId(Player player, long object);

}
