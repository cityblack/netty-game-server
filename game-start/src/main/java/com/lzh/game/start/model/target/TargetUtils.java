package com.lzh.game.start.model.target;

import com.lzh.game.start.model.player.Player;
import com.lzh.game.start.model.target.handler.AbstractTargetHandler;
import com.lzh.game.start.model.target.model.TargetModelStrategy;
import com.lzh.game.start.model.target.model.TargetModelSign;
import com.lzh.game.start.util.SpringContext;

import java.util.HashSet;
import java.util.Set;

/**
 * 目标生成工具
 */
public class TargetUtils {
    /**
     * 目标入口 与resource关联
     * @param player
     * @param id
     * @param targetDef
     * @param modelSign
     */
    public static void produceTarget(Player player, int id, TargetDef[] targetDef, TargetModelSign modelSign) {

        Set<Target> targets = new HashSet<>(targetDef.length);
        TargetModelStrategy model = targetModel(modelSign);

        for (TargetDef def: targetDef) {
            Target target = model.crateAndSaveTarget(player, id, def);
            TargetType type = target.getTargetType();
            AbstractTargetHandler handler = handler(type);
            handler.init(player, target);
            if (target.isComplete()) {
                targets.add(target);
            }
        }
        if (!targets.isEmpty()) {
            model.onTargetComplete(player, targets);
        }
    }

    private static TargetModelStrategy targetModel(TargetModelSign sign) {
        return SpringContext.singleTon().getTargetModelManage().getTargetModel(sign);
    }

    private static AbstractTargetHandler handler(TargetType type) {
        return SpringContext.singleTon().getTargetHandlerManage().handler(type);
    }
}
