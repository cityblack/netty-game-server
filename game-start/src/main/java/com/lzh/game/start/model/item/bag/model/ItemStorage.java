package com.lzh.game.start.model.item.bag.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lzh.game.business.grid.DefaultMarkGrid;
import com.lzh.game.start.model.item.model.AbstractItem;
import com.lzh.game.start.model.item.resource.ItemResource;
import com.lzh.game.start.model.item.service.ItemResourceManage;
import com.lzh.game.start.util.ApplicationUtils;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ItemStorage extends DefaultMarkGrid<AbstractItem> {

    /**
     * 消耗物品 当物品不足的时候 返回空列表
     *
     * @param itemModel
     * @param num
     * @param whenReduce 消耗物品的时候
     * @return
     */
    public List<RemoveItem> reduceItems(int itemModel, int num, Consumer<RemoveItem> whenReduce) {
        int needReduce = num;
        List<RemoveItem> removeItems = new ArrayList<>();
        for (int i = 0; i < this.items.length; i++) {
            if (!indexHasItem(i)) {
                continue;
            }
            AbstractItem item = getItem(i);
            if (item.getResourceId() == itemModel) {
                removeItems.add(RemoveItem.of(item, needReduce, i));
                needReduce -= item.getNum();
            }
            if (needReduce <= 0) {
                break;
            }
        }
        if (needReduce > 0) {
            return Collections.emptyList();
        }
        removeItems.forEach(e -> {
            doRemove(e.getSourceIndex(), e.getNum());
            whenReduce.accept(e);
        });
        return removeItems;
    }

    /**
     * 根据物品唯一id消耗 当物品不足的时候返回空
     *
     * @param itemId
     * @param num
     * @return
     */
    public RemoveItem reduceItemById(long itemId, int num) {

        for (int i = 0; i < this.items.length; i++) {
            if (indexHasItem(i)) {
                continue;
            }
            AbstractItem item = getItem(i);
            if (item.getObjectId() == itemId) {
                if (item.getNum() >= num) {
                    item.setNum(item.getNum() - num);
                    if (item.getNum() == 0) {
                        signEmptyGrid(i);
                    }
                    return RemoveItem.of(item, num, i);
                }
            }
        }
        return null;
    }

    private void doRemove(int index, int num) {
        AbstractItem item = getItem(index);
        item.setNum(item.getNum() - num);
        if (item.getNum() <= 0) {
            removeItem(index);
        }
        mark(index);
    }

    public RemoveItem removeItem(long itemId) {
        for (int i = 0; i < this.items.length; i++) {
            if (indexHasItem(i)) {
                continue;
            }
            AbstractItem item = getItem(i);
            if (item.getObjectId() == itemId) {
                signEmptyGrid(i);
                return RemoveItem.of(item, item.getNum(), i);
            }
        }
        return null;
    }

    public int itemNeedGridLen(int itemModel, final int num) {
        int moreNum = num;
        if (canStack(itemModel)) {
            for (int i = 0; i < this.items.length; i++) {
                if (indexHasItem(i)) {
                    continue;
                }
                AbstractItem item = getItem(i);
                if (item.getResourceId() == itemModel && item.canStack()) {
                    moreNum -= item.canStackNumMore();
                    if (moreNum <= 0) {
                        return 0;
                    }
                }
            }
        }
        return needMoreGrid(itemModel, moreNum);
    }

    public int itemNeedGridLen(List<AbstractItem> items) {
        var idAndNum = AbstractItem.itemToIdAndNum(items);
        int len = 0;
        for (Map.Entry<Integer, Integer> entry : idAndNum.entrySet()) {
            len += itemNeedGridLen(entry.getKey(), entry.getValue());
        }
        return len;
    }

    @JsonIgnore
    public boolean isEnoughItems(int itemModel, int num) {
        int hasNum = 0;

        for (Map.Entry<Integer, AbstractItem> entry : loadedItems().entrySet()) {
            if (entry.getValue().getResourceId() == itemModel) {
                hasNum += entry.getValue().getNum();
                if (hasNum >= num) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean canStack(int itemModel) {
        return getItemResource(itemModel).canStack();
    }

    private int needMoreGrid(int itemModel, int num) {
        int stack = Math.min(getItemResource(itemModel).getStack(), 1);
        return num / stack;
    }

    @JsonIgnore
    protected ItemResource getItemResource(int itemModel) {
        return ApplicationUtils.getBean(ItemResourceManage.class).findItemResourceById(itemModel);
    }

    @Override
    public int addItem(AbstractItem item) {
        if (item.canStack()) {
            int firstEmpty = -1;
            for (int i = 0; i < this.items.length; i++) {
                if (!indexHasItem(i)) {
                   if (firstEmpty == -1) {
                       firstEmpty = i;
                   }
                   continue;
                }
                AbstractItem old = getItem(i);
                if (old.canStack() && old.getNum() + item.getNum() <= old.getResource().getStack()) {
                    continue;
                }
                stackedItem(old, item.getNum(), i);
                return i;
            }
            if (firstEmpty == -1) {
                setGrid(firstEmpty, item);
            }
            return firstEmpty;
        } else {
            return super.addItem(item);
        }
    }

    public List<AbstractItem> findItemsByItemModelId(int itemId) {
        return Stream.of(getItems())
                .map(e -> (AbstractItem)e)
                .filter(e -> e.getResourceId() == itemId)
                .collect(Collectors.toList());
    }

    public AbstractItem findItemByObjectId(long objectId) {
        return  Stream.of(getItems())
                .map(e -> (AbstractItem)e)
                .filter(e -> e.getObjectId() == objectId)
                .findFirst()
                .orElse(null);
    }

    private void stackedItem(AbstractItem sourceItem, int num, int index) {
        sourceItem.addNum(num);
        getMark().set(index);
    }

    public ItemStorage() {

    }

    protected ItemStorage(int size) {
        super(size);
    }

    public static ItemStorage of() {
        return new ItemStorage(512);
    }

    public static ItemStorage of(int size) {
        ItemStorage storage = new ItemStorage(size);
        return storage;
    }

}
