package com.lzh.game.start.model.target;

import com.lzh.game.common.ApplicationUtils;
import com.lzh.game.start.model.target.handler.AbstractTargetHandler;
import com.lzh.game.start.model.target.handler.TargetHandlerManage;
import com.lzh.game.start.model.target.model.TargetModelManage;
import com.lzh.game.start.model.target.model.TargetModelSign;
import com.lzh.game.start.model.target.model.TargetModelStrategy;
import com.lzh.game.start.model.player.Player;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

/**
 * Target Tools,Used in achievement goal and so on
 */
@Slf4j
public class TargetUtils {
    /**
     * Produce target with targetDef
     *
     * @param player
     * @param id
     * @param targetDef
     * @param modelSign
     */
    public static void produceTarget(Player player, int id, TargetDef[] targetDef, TargetModelSign modelSign) {

        Set<Target> targets = new HashSet<>(targetDef.length);
        Set<Target> changes = new HashSet<>(targetDef.length);

        TargetModelStrategy model = targetModel(modelSign);

        for (TargetDef def : targetDef) {
            Target target = model.crateAndSaveTarget(player, id, def);
            TargetType type = target.getTargetType();
            AbstractTargetHandler handler = handler(type);
            handler.init(player, target);
            changes.add(target);
            if (target.isComplete()) {
                targets.add(target);
            }
        }
        if (!changes.isEmpty()) {
            model.onTargetProcessChange(player, changes);
        }
        if (!targets.isEmpty()) {
            model.onTargetComplete(player, targets);
        }
    }

    private static TargetModelStrategy targetModel(TargetModelSign sign) {
        return ApplicationUtils.getBean(TargetModelManage.class).getTargetModel(sign);
    }

    private static AbstractTargetHandler handler(TargetType type) {
        return ApplicationUtils.getBean(TargetHandlerManage.class).handler(type);
    }

    /**
     * For each all target model
     *
     * @param player
     * @param process -- target current progress value
     */
    public static void foreachTargetModel(Player player, Function<Target, Long> process, TargetType type) {

        ApplicationUtils.getBean(TargetModelManage.class)
                .stream()
                .filter(targetModel -> !targetModel.isCompleted(player))
                .forEach(targetModel -> {
                    Set<Target> targets = targetModel.getTarget(player, type);
                    if (targets.isEmpty()) {
                        return;
                    }
                    Set<Target> change = new HashSet<>(targets.size());
                    Set<Target> completed = new HashSet<>(targets.size());

                    targets.stream()
                            .filter(target -> !target.isComplete())
                            .forEach(target -> doForEachTarget(target, change, completed, process));
                    if (!change.isEmpty()) {
                        targetModel.onTargetProcessChange(player, change);
                    }
                    if (!completed.isEmpty()) {
                        targetModel.onTargetComplete(player, completed);
                    }
                });
    }

    protected static void doForEachTarget(Target target, Set<Target> change, Set<Target> completed, Function<Target, Long> process) {
        try {
            long value = process.apply(target);
            if (value > target.getCurrentValue()) {
                target.setCurrentValue(value);
                change.add(target);
            }
            if (target.isComplete()) {
                completed.add(target);
            }

        } catch (Exception e) {
            log.error("Target处理异常: ", e);
        }
    }
}
