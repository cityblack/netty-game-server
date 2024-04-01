package com.lzh.game.start.model.target;

import com.lzh.game.start.util.ApplicationUtils;
import com.lzh.game.start.model.player.Player;
import com.lzh.game.start.model.target.handler.AbstractTargetHandler;
import com.lzh.game.start.model.target.handler.TargetHandlerManage;
import com.lzh.game.start.model.target.model.TargetCombination;
import com.lzh.game.start.model.target.model.TargetCombinationEnum;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Target Tools,Used in achievement goal and so on
 */
@Slf4j
public class TargetUtils {

    private static final BiFunction<Player, TargetDef, ? extends Target> DEFAULT_CREATE_TARGET
            = (player, targetDef) -> new Target(targetDef);
    /**
     * Produce target with targetDef
     *
     * @param player
     * @param id
     */
    public static MultiTarget produceTarget(Player player, int id, MultiTargetDef def) {
        return produceTarget(player, id, def, DEFAULT_CREATE_TARGET);
    }

    public static <T extends Target>MultiTarget produceTarget(Player player, int id, MultiTargetDef def, BiFunction<Player, TargetDef, T> createTarget) {
        Target[] targets = new Target[def.getTargets().length];
        for (int i = 0; i < def.getTargets().length; i++) {
            TargetDef targetDef = def.getTargets()[i];
            Target target = createTarget.apply(player, targetDef);
            TargetType type = target.getTargetType();
            AbstractTargetHandler handler = handler(type);
            handler.init(player, target);
        }
        return MultiTarget.of(id, targets, def.isUseOr());
    }

    /**
     * Use history
     */
    public static void tryMultiTargetComplete(Player player, TargetCombination combination) {
        Set<MultiTarget> targets = combination.getTarget(player);
        if (Objects.isNull(targets) || targets.isEmpty()) {
            return;
        }
        Set<MultiTarget> completed = new HashSet<>(targets.size());

        for (MultiTarget target : targets) {
            if (target.isCompleted()) {
                continue;
            }
            boolean complete = target.isOrCompose()
                    ? Stream.of(target.getTargets()).anyMatch(Target::isComplete)
                    : Stream.of(target.getTargets()).allMatch(Target::isCompleted);
            if (complete) {
                target.setStatus(TargetStatus.COMPLETED.getStatus());
                completed.add(target);
            }
        }
        if (!completed.isEmpty()) {
            combination.onTargetComplete(player, completed);
        }
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
        for (TargetCombination value : TargetCombinationEnum.values()) {
            if (value.isFinish(player)) {
                continue;
            }
            Set<MultiTarget> targets = value.getTarget(player);
            if (targets.isEmpty()) {
                continue;
            }
            Set<MultiTarget> change = new HashSet<>(targets.size());
            Set<MultiTarget> completed = new HashSet<>(targets.size());

            for (MultiTarget target : targets) {
                if (target.isCompleted()) {
                    continue;
                }
                if (!target.hasTypeTarget(type)) {
                    continue;
                }
                doForEachTarget(target, change, completed, process);
            }

            if (!change.isEmpty()) {
                value.onTargetProcessChange(player, change);
            }
            if (!completed.isEmpty()) {
                value.onTargetComplete(player, completed);
            }
        }
    }


    protected static void doForEachTarget(MultiTarget multiTarget, Set<MultiTarget> change, Set<MultiTarget> completed, Function<Target, Long> process) {
        try {
            for (Target target : multiTarget.getTargets()) {
                if (target.isCompleted()) {
                    continue;
                }
                long value = process.apply(target);
                if (value > target.getCurrentValue()) {
                    target.setCurrentValue(value);
                    change.add(multiTarget);
                }
                if (target.isComplete()) {
                    target.setStatus(TargetStatus.COMPLETED.getStatus());
                }
            }

            Stream<Target> stream = Stream.of(multiTarget.getTargets());
            boolean complete = multiTarget.isOrCompose()
                    ? stream.anyMatch(Target::isComplete)
                    : stream.allMatch(Target::isCompleted);
            if (complete) {
                multiTarget.setStatus(TargetStatus.COMPLETED.getStatus());
                completed.add(multiTarget);
            }
        } catch (Exception e) {
            log.error("Target处理异常: ", e);
        }
    }
}
