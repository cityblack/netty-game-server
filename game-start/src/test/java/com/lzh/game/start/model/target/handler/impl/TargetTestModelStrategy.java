package com.lzh.game.start.model.target.handler.impl;

import com.lzh.game.start.model.player.Player;
import com.lzh.game.start.model.target.Target;
import com.lzh.game.start.model.target.TargetDef;
import com.lzh.game.start.model.target.TargetType;
import com.lzh.game.start.model.target.model.TargetModelStrategy;
import com.lzh.game.start.model.target.model.TargetModelSign;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@Slf4j
public class TargetTestModelStrategy implements TargetModelStrategy {

    private Set<Target> targets = new HashSet<>();

    @Override
    public TargetModelSign modelSign() {
        return TargetModelSign.ACHIEVEMENT;
    }

    @Override
    public Target crateAndSaveTarget(Player player, int id, TargetDef targetDef) {
        Target target = new Target(id, targetDef);
        targets.add(target);
        return target;
    }

    @Override
    public Set<Target> getTarget(Player player, TargetType targetType) {
        return targets;
    }

    @Override
    public void onTargetProcessChange(Player player, Set<Target> target) {
        log.debug("Target change:{}", target);
    }

    @Override
    public void onTargetComplete(Player player, Set<Target> target) {
        log.debug("Target completed:{}", target);
    }

    @Override
    public boolean isCompleted(Player player) {
        return false;
    }
}
