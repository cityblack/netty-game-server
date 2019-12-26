package com.lzh.game.start.model.item.model;

import com.lzh.game.start.log.LogReason;
import com.lzh.game.start.model.player.Player;

import java.util.Map;

public class UseAbleItem extends AbstractItem {

    /**
     * 使用验证
     * @param player
     */
    public void useVerify(Player player, Map<String, String> params) {
        verify(player);
    }

    /**
     * 使用结果
     * @param player
     */
    public void useEffect(Player player, Map<String, String> params, LogReason logReason) {
        consume().consume(player, logReason);
        reward().reward(player, logReason);
    }

    protected void verify(Player player) {
        verifyCondition(player);
        verifyConsumer(player);
        verifyReward(player);
    }

    protected void verifyCondition(Player player) {
        condition().verifyThrow(player);
    }

    protected void verifyReward(Player player) {
        reward().verifyThrow(player);
    }

    protected void verifyConsumer(Player player) {
        consume().verifyThrow(player);
    }

}
