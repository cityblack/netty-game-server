package com.lzh.game.start.model.item.bag.service.impl;

import com.lzh.game.start.log.LogReason;
import com.lzh.game.start.model.i18n.RequestException;
import com.lzh.game.start.model.item.bag.dao.BagDataManage;
import com.lzh.game.start.model.item.bag.exception.NotEnoughItemException;
import com.lzh.game.start.model.item.bag.model.ItemStorage;
import com.lzh.game.start.model.item.bag.model.RemoveItem;
import com.lzh.game.start.model.item.bag.service.PlayerBagService;
import com.lzh.game.start.model.i18n.I18n;
import com.lzh.game.start.model.item.model.AbstractItem;
import com.lzh.game.start.model.item.resource.ItemResource;
import com.lzh.game.start.model.item.service.ItemResourceManage;
import com.lzh.game.start.model.player.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class PlayerBagServiceImpl implements PlayerBagService {

    @Autowired
    private ItemResourceManage itemResourceManage;

    @Autowired
    private BagDataManage bagDataManage;

    @Override
    public List<RemoveItem> reduceItems(Player player, int itemModel, int num, LogReason logReason) throws NotEnoughItemException {

//        ItemStorage storage = getPack(player);
//        List<RemoveItem> removeItems = storage.reduceItems(itemModel, num, removeItem -> ItemLog.reduceItem(player, removeItem, logReason));
//
//        if (removeItems.isEmpty()) {
//            throw new NotEnoughItemException(itemModel, num);
//        }
//        update(player);
        return null;
    }

    @Override
    public RemoveItem reduceItemById(Player player, long itemId, int num, LogReason logReason) throws NotEnoughItemException {

        ItemStorage storage = getPack(player);
        RemoveItem removeItem = storage.reduceItemById(itemId, num);

        if (Objects.isNull(removeItem)) {
            throw new NotEnoughItemException(itemId, num);
        }
        update(player);
        return removeItem;
    }

    @Override
    public RemoveItem removeItem(Player player, long itemId, LogReason logReason) throws NotEnoughItemException {

        ItemStorage itemStorage = getPack(player);
        RemoveItem remove = itemStorage.removeItem(itemId);
        if (Objects.isNull(remove)) {
            throw new NotEnoughItemException(itemId, 1);
        }
        update(player);
        return remove;
    }

    @Override
    public int itemNeedGridLen(Player player, List<AbstractItem> items) {
        return getPack(player).itemNeedGridLen(items);
    }

    @Override
    public int itemNeedGridLen(Player player, int itemModel, int num) {

        return getPack(player).itemNeedGridLen(itemModel, num);
    }

    @Override
    public void addItem(Player player, List<AbstractItem> abstractItems, LogReason logReason) {
        ItemStorage storage = getPack(player);

//        storage.addItems(abstractItems
//                , item -> ItemLog.addItem(player, item, logReason)
//                , item -> ItemLog.lossItem(player, item, logReason));
        update(player);
        sendChange(storage.getChange());
        storage.clearMark();
    }

    @Override
    public List<AbstractItem> getItemsByItemModelId(Player player, int itemId) {
        return getPack(player).findItemsByItemModelId(itemId);
    }

    @Override
    public AbstractItem getItemByObjectId(Player player, long objectId) {
        return getPack(player).findItemByObjectId(objectId);
    }

    @Override
    public boolean isEnoughItems(Player player, int itemModel, int num) {

        return getPack(player).isEnoughItems(itemModel, num);
    }

    @Override
    public void isEnoughItemsThrow(Player player, int itemModel, int num) throws RequestException {
        boolean enough = isEnoughItems(player, itemModel, num);
        if (!enough) {
            throw new RequestException(I18n.NOT_ENOUGH_ITEMS);
        }
    }

    @Override
    public boolean hasMoreGrid(Player player, int gridSize) {
        return getPack(player).isEnoughSize(gridSize);
    }

    @Override
    public boolean isEnoughGrid(Player player, List<AbstractItem> items) {
        int len = itemNeedGridLen(player, items);
        if (len <= 0) {
            return true;
        }
        return hasMoreGrid(player, len);
    }

    private ItemStorage getPack(Player player) {
        return player.getBag().getPack();
    }

    @Override
    public void update(Player player) {
        bagDataManage.updateBag(player.getBag());
    }

    protected ItemResource getItemResource(int itemModel) {
        return itemResourceManage.findItemResourceById(itemModel);
    }

    private void sendChange(Map<Integer, AbstractItem> change) {

    }

}
