package com.lzh.game.start.model.item.bag.service;

import com.lzh.game.start.model.i18n.I18n;
import com.lzh.game.start.model.i18n.RequestException;
import com.lzh.game.start.model.item.model.AbstractItem;
import com.lzh.game.start.model.player.Player;

import java.util.List;
import java.util.Map;

public interface ItemVerify {
    /**
     * 是否还够物品
     * @param itemModel
     * @param num
     * @return
     */
    boolean isEnoughItems(Player player, int itemModel, int num);

    /**
     *
     * @param itemModel
     * @param num
     */
    void isEnoughItemsThrow(Player player, int itemModel, int num) throws RequestException;
    /**
     * 背包是否还足够放物品
     * @param itemModel
     * @param num
     * @return
     */
    boolean isEnoughGrid(Player player, int itemModel, int num);

    /**
     * 是否还足够几个格子
     * @param gridSize
     * @return
     */
    boolean hasMoreGrid(Player player, int gridSize);

    /**
     * 背包是否还足够放物品
     * @param player
     * @param itemAndNum
     * @return
     */
    boolean isEnoughGrid(Player player, Map<Integer, Integer> itemAndNum);

    /**
     * 是否还有足够的空间
     * 根据物品判断
     * @return
     */
    boolean isEnoughGrid(Player player, List<AbstractItem> items);

    /**
     * 是否还有足够的空间
     * @param player
     * @param items
     */
    default void isEnoughGridThrow(Player player, List<AbstractItem> items) throws RequestException {
        if (!isEnoughGrid(player, items)) {
            throw new RequestException(I18n.NOT_ENOUGH_GRID);
        }
    }
}
