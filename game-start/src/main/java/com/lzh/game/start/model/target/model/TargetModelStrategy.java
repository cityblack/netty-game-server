package com.lzh.game.start.model.target.model;

import com.lzh.game.start.model.player.Player;
import com.lzh.game.start.model.target.Target;
import com.lzh.game.start.model.target.TargetDef;
import com.lzh.game.start.model.target.TargetType;

import java.util.Set;

/**
 * 目标不同模块 策略模式
 * 不同的目标模块需要自己实现并且注册spring bean
 */
public interface TargetModelStrategy {

    /**
     * 模块标识
     * @return
     */
    TargetModelSign modelSign();
    /**
     * 不同模块自己创建存储Target
     * @param player
     * @param id Target唯一标识
     * @param targetDef
     * @return
     */
    Target crateAndSaveTarget(Player player, int id, TargetDef targetDef);

    /**
     * 不同模块自己存储Target 由该接口获取
     * @param player
     * @param targetType
     * @return
     */
    Set<Target> getTarget(Player player, TargetType targetType);

    /**
     * 处理目标进度改变事件
     * @param player
     * @param target
     */
    void onTargetProcessChange(Player player, Set<Target> target);

    /**
     * 处理目标完成
     * @param player
     * @param target
     */
    void onTargetComplete(Player player, Set<Target> target);

    /**
     * 给特殊的预留接口
     * 返回true则不再处理该模块的Target
     * @param player
     * @return
     */
    default boolean isCompleted(Player player) {
         return false;
    }
}
