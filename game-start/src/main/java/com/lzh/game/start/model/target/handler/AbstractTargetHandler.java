package com.lzh.game.start.model.target.handler;

import com.lzh.game.start.model.player.Player;
import com.lzh.game.start.model.target.Target;
import com.lzh.game.start.model.target.TargetType;
import com.lzh.game.start.util.SpringContext;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

@Slf4j
public abstract class AbstractTargetHandler {

    public abstract void init(Player player, Target target);

    public abstract TargetType type();

    /**
     * 遍历所有模块的Target
     * @param player
     * @param process -- 返回Target的当前进度
     */
    protected void foreachTargetModel(Player player, Function<Target, Long> process) {

        SpringContext.singleTon().getTargetModelManage()
                .stream()
                .filter(targetModel -> !targetModel.isCompleted(player))
                .forEach(targetModel -> {
                    Set<Target> targets = targetModel.getTarget(player, type());
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

    private void doForEachTarget(Target target, Set<Target> change, Set<Target> completed, Function<Target, Long> process) {
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
