package com.lzh.game.start.model.item.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lzh.game.start.util.ApplicationUtils;
import com.lzh.game.start.model.core.util.condition.Condition;
import com.lzh.game.start.model.core.util.consume.Consume;
import com.lzh.game.start.model.core.util.reward.Reward;
import com.lzh.game.start.model.item.resource.ItemResource;
import com.lzh.game.start.model.item.service.ItemResourceManage;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractItem {

    @Getter
    @Setter
    private long objectId;

    @Getter
    @Setter
    private int resourceId;

    private short num;

    public long getObjectId() {
        return objectId;
    }

    @JsonIgnore
    public ItemResource getResource() {
        return ApplicationUtils.getBean(ItemResourceManage.class).findItemResourceById(resourceId);
    }

    public Reward reward() {
        return getResource().getReward();
    }

    public Consume consume() {
        return getResource().getConsume();
    }

    public Condition condition() {
        return getResource().getCondition();
    }

    @JsonIgnore
    // accord resource id to superpose
    public boolean canStack() {
        return getResource().canStack();
    }

    public int canStackNumMore() {
        if (!canStack()) {
            return 0;
        }
        return getResource().getStack() - num;
    }

    public void reduceNum(int num) {
        this.num -= num;
    }

    public void addNum(int num) {
        this.num++;
    }

    @JsonIgnore
    public String getName() {
        return getResource().getName();
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = (short) num;
    }

    public static Map<Integer, Integer> itemToIdAndNum(List<AbstractItem> items) {
        Map<Integer, Integer> map = new HashMap<>();
        for (AbstractItem item : items) {
            map.merge(item.getResourceId(), item.getNum(), Integer::sum);
        }
        return map;
    }
}
