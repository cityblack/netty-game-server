package com.lzh.game.start.model.target.model;

import com.lzh.game.start.model.player.Player;
import com.lzh.game.start.model.target.MultiTarget;

import java.util.Set;

public enum TargetCombinationEnum implements TargetCombination {
    // 成就
    ACHIEVEMENT() {

        @Override
        public Set<MultiTarget> getTarget(Player player) {
            return null;
        }

        @Override
        public void onTargetProcessChange(Player player, Set<MultiTarget> target) {

        }

        @Override
        public void onTargetComplete(Player player, Set<MultiTarget> target) {

        }
    }
    ;

}
