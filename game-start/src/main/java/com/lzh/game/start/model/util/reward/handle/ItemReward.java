package com.lzh.game.start.model.util.reward.handle;

import com.lzh.game.start.util.ApplicationUtils;
import com.lzh.game.start.log.LogReason;
import com.lzh.game.start.model.util.VerifyResult;
import com.lzh.game.start.model.util.reward.AbstractReward;
import com.lzh.game.start.model.item.bag.service.PlayerBagService;
import com.lzh.game.start.model.item.model.AbstractItem;
import com.lzh.game.start.model.item.service.ItemService;
import com.lzh.game.start.model.player.Player;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 *
 */
public class ItemReward extends AbstractReward {

    @Getter
    @Setter
    private int itemId;

    @Getter
    @Setter
    private int num;

    @Override
    protected void doParse(String value) {
        String[] strings = value.split("_");
        itemId = Integer.parseInt(strings[0]);
        num = Integer.parseInt(strings[1]);
    }

    @Override
    public void doVerify(Object param, VerifyResult result, int multiple) {
        int count = num * multiple;
        Map<Object, Object> context = result.getVerifyContext();
        collectItems(itemId, count, context);
        if (result.isLastContext()) {
            verify(param, context);
        }
    }

    private void collectItems(int itemModel, int num, Map<Object, Object> context) {
        int itemSum = (int) context.getOrDefault(itemModel, 0) + num;
        context.put(itemModel, itemSum);
    }

    private void verify(Object o, Map<Object, Object> context) {
        if (o instanceof Player player) {
            for (Map.Entry<Object, Object> entry: context.entrySet()) {
                ApplicationUtils.getBean(PlayerBagService.class).isEnoughGrid(player, (int)entry.getKey(), (int)entry.getValue());
            }
        }
    }

    @Override
    public void reward(Object o, int multiple, LogReason logReason) {
        if (o instanceof Player player) {
            int count = num * multiple;
            List<AbstractItem> items = ApplicationUtils.getBean(ItemService.class).createItem(itemId, count);
            ApplicationUtils.getBean(PlayerBagService.class).addItem(player, items, logReason);
        }
    }
}
