package com.lzh.game.start.model.core.util.consume.handle;


import com.lzh.game.start.util.ApplicationUtils;
import com.lzh.game.start.log.LogReason;
import com.lzh.game.start.model.item.bag.exception.NotEnoughItemException;
import com.lzh.game.start.model.core.util.consume.AbstractConsume;
import com.lzh.game.start.model.item.bag.service.PlayerBagService;
import com.lzh.game.start.model.player.Player;
import com.lzh.game.start.model.core.util.VerifyResult;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * Item consumer handler, config format -> {type:"ITEM",value:"1000_1"} -> 1000 means itemId, 1 means num
 */
public class ItemConsume extends AbstractConsume {

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
    public void doVerify(Object data, VerifyResult result, int multiple) {
        if (data instanceof Player player) {
            int count = num * multiple;
            Map<Object, Object> context = result.getVerifyContext();
            collectItems(itemId, count, context);
            if (result.isLastContext()) {
                verify(player, context);
            }
        }
    }

    private void collectItems(int itemModel, int num, Map<Object, Object> context) {
        int itemSum = (int) context.getOrDefault(itemModel, 0) + num;
        context.put(itemModel, itemSum);
    }

    private void verify(Player player, Map<Object, Object> context) {
        for (Map.Entry<Object, Object> entry: context.entrySet()) {
            ApplicationUtils.getBean(PlayerBagService.class).isEnoughItemsThrow(player, (int)entry.getKey(), (int)entry.getValue());
        }
    }

    @Override
    public void consume(Object data, int multiple, LogReason logReason) {
        if (data instanceof Player player) {
            int count = num * multiple;
            try {
                ApplicationUtils.getBean(PlayerBagService.class).reduceItems(player, itemId, count, logReason);
            } catch (NotEnoughItemException e) {
                e.printStackTrace();
            }
        }
    }
}
