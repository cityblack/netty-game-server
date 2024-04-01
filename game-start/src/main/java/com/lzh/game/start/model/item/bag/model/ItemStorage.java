package com.lzh.game.start.model.item.bag.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lzh.game.start.util.ApplicationUtils;
import com.lzh.game.framework.grid.MarkGridTable;
import com.lzh.game.start.model.item.model.AbstractItem;
import com.lzh.game.start.model.item.resource.ItemResource;
import com.lzh.game.start.model.item.service.ItemResourceManage;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ItemStorage extends MarkGridTable<AbstractItem> {

    /**
     * 消耗物品 当物品不足的时候 返回空列表
     * @param itemModel
     * @param num
     * @param whenReduce 消耗物品的时候
     * @return
     */
    public List<RemoveItem> reduceItems(int itemModel, int num, Consumer<RemoveItem> whenReduce) {
        int needReduce = num;
        List<RemoveItem> removeItems = new ArrayList<>();

        for (Map.Entry<Integer, AbstractItem> entry: getItems().entrySet()) {
            if (needReduce == 0) {
                break;
            }
            AbstractItem item = entry.getValue();
            if (item.getResourceId() == itemModel) {
                if (item.getNum() > needReduce) {
                    removeItems.add(RemoveItem.of(item, needReduce, entry.getKey()));
                } else if (item.getNum() == needReduce){
                    removeItems.add(RemoveItem.of(item, needReduce, entry.getKey()));
                } else {
                    needReduce -= item.getNum();
                }
            }
        }
        if (needReduce > 0) {
            return Collections.EMPTY_LIST;
        }
        removeItems.forEach(e -> {
            doRemove(e.getSourceIndex(), e.getNum());
            whenReduce.accept(e);
        });
        return removeItems;
    }

    /**
     * 根据物品唯一id消耗 当物品不足的时候返回空
     * @param itemId
     * @param num
     * @return
     */
    public RemoveItem reduceItemById(long itemId, int num) {

        for (Map.Entry<Integer, AbstractItem> entry: getItems().entrySet()) {

            AbstractItem item = entry.getValue();
            if (item.getObjectId() == itemId) {
                if (item.getNum() >= num) {
                    return RemoveItem.of(item, num, entry.getKey());
                }
                break;
            }
        }
        return null;
    }

    private void doRemove(int index, int num) {
        AbstractItem item = getItem(index);
        item.setNum((short) (item.getNum() - num));
        if (item.getNum() <= 0) {
            removeItem(index);
        }
    }

    public RemoveItem removeItem(long itemId) {

        for (Map.Entry<Integer, AbstractItem> entry: getItems().entrySet()) {
            if (entry.getValue().getObjectId() == itemId) {
                AbstractItem item = removeItem(entry.getKey());
                return RemoveItem.of(item, item.getNum(), entry.getKey());
            }
        }

        return null;
    }

    public int itemNeedGridLen(int itemModel, int num) {
        int moreNum = num;

        if (canStack(itemModel)) {

            for (Map.Entry<Integer, AbstractItem> entry: getItems().entrySet()) {
                AbstractItem item = entry.getValue();
                if (item.getResourceId() == itemModel && item.canStack()) {
                    moreNum = item.canStackNumMore();
                    if (moreNum <= 0) {
                        return 0;
                    }
                }
            }
        }

        return needMoreGrid(itemModel, moreNum);
    }

    public int itemNeedGridLen(List<AbstractItem> items) {
        int len = 0;
        Map<Long, Integer> canPutted = new HashMap<>(4);

        for (AbstractItem item: items) {
            if (!item.canStack()) {
                len++;
                continue;
            }
            int moreNum = item.getNum();
            for (Map.Entry<Integer, AbstractItem> entry: getItems().entrySet()) {
                if (moreNum <= 0) {
                    break;
                }
                AbstractItem stackItem = entry.getValue();
                if (!stackItem.canStack()) {
                    continue;
                }
                if (stackItem.getResourceId() == item.getResourceId()) {
                    int canPutSize = canPutted.getOrDefault(stackItem.getObjectId(), stackItem.canStackNumMore());
                    if (canPutSize >= moreNum) {
                        moreNum = 0;
                        canPutted.put(stackItem.getObjectId(), canPutSize - moreNum);
                    } else {
                        moreNum -= canPutSize;
                        canPutted.put(stackItem.getObjectId(), 0);
                    }
                }
            }
            if (moreNum > 0) {
                len++;
            }
        }
        return len;
    }

    @JsonIgnore
    public boolean isEnoughItems(int itemModel, int num) {
        int hasNum = 0;
        for (Map.Entry<Integer, AbstractItem> entry: loadedItems().entrySet()) {
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
        return num / getItemResource(itemModel).getStack() + 1;
    }

    @JsonIgnore
    protected ItemResource getItemResource(int itemModel) {
        return ApplicationUtils.getBean(ItemResourceManage.class).findItemResourceById(itemModel);
    }

    @Deprecated
    @Override
    public int addItem(AbstractItem item) {
        return super.addItem(item);
    }

    public void addItems(List<AbstractItem> items, Consumer<AbstractItem> addOk, Consumer<AbstractItem> addFailure) {

        items.forEach(item -> {
            int itemModel = item.getResourceId();

            if (item.canStack()) {
                for (Map.Entry<Integer, AbstractItem> entry: getItems().entrySet()) {

                    if (item.getNum() <= 0) {
                        return;
                    }
                    AbstractItem stackedItem = entry.getValue();
                    if (stackedItem.getResourceId() == itemModel && stackedItem.canStack()) {
                        int hasMoreStack = item.canStackNumMore();
                        if (hasMoreStack <= 0) {
                            continue;
                        }
                        int canAdd = item.getNum() - hasMoreStack;
                        if (hasMoreStack >= item.getNum()) {
                            stackedItem(stackedItem, item.getNum(), entry.getKey());
                            addOk.accept(item);
                            item.reduceNum(item.getNum());
                            return;
                        }

                        stackedItem(stackedItem, canAdd, entry.getKey());
                        addOk.accept(item);
                        item.reduceNum(canAdd);
                    }
                }
            }

            if (item.getNum() > 0) {
                int index = addItem(item);
                if (index < 0) {
                    if (Objects.nonNull(addFailure)) {
                        addFailure.accept(item);
                    }
                } else {
                    addOk.accept(item);
                }
            }
        });

    }

    public List<AbstractItem> findItemsByItemModelId(int itemId) {
        return getItems()
                .values()
                .stream()
                .filter(e -> e.getResourceId() == itemId)
                .collect(Collectors.toList());
    }

    public AbstractItem findItemByObjectId(long objectId) {
        return getItems().values()
                .stream()
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
        return new ItemStorage(DEFAULT_TABLE_SIZE);
    }

    public static ItemStorage of(int size) {
        ItemStorage storage = new ItemStorage(size);
        return storage;
    }

}
