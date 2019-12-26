package com.lzh.game.start.model.target.handler.impl;

import com.lzh.game.common.event.SubscribeListener;
import com.lzh.game.start.model.player.Player;
import com.lzh.game.start.model.target.Target;
import com.lzh.game.start.model.target.TargetType;
import com.lzh.game.start.model.target.handler.AbstractTargetHandler;
import org.springframework.stereotype.Component;

/**
 * 大于等于等级Target
 */
@Component
public class LevelEtTargetHandler extends AbstractTargetHandler {

    @Override
    public void init(Player player, Target target) {
        target.setCurrentValue(player.getLevel());
    }

    @Override
    public TargetType type() {
        return TargetType.LEVEL_ET;
    }

    public void onLevelUp() {

    }

    private void doLevelUp(Player player) {
        foreachTargetModel(player, target -> Long.valueOf(player.getLevel()));
    }
}
