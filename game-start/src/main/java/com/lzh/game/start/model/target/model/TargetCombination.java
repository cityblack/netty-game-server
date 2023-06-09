package com.lzh.game.start.model.target.model;

import com.lzh.game.start.model.player.Player;
import com.lzh.game.start.model.target.MultiTarget;

import java.util.Set;

public interface TargetCombination {

    /**
     *
     * @param player
     * @return
     */
    Set<MultiTarget> getTarget(Player player);

    /**
     * 处理目标进度改变事件
     * @param player
     * @param target
     */
    void onTargetProcessChange(Player player, Set<MultiTarget> target);

    /**
     * 处理目标完成
     * @param player
     * @param target
     */
    void onTargetComplete(Player player, Set<MultiTarget> target);

    /**
     * 给特殊的预留接口
     * 返回true则不再处理该模块的Target
     * @param player
     * @return
     */
    default boolean isFinish(Player player) {
         return false;
    }
}
