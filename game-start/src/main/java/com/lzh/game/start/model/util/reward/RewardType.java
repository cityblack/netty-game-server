package com.lzh.game.start.model.util.reward;

import com.lzh.game.start.model.util.reward.handle.AndReward;
import com.lzh.game.start.model.util.reward.handle.ItemReward;

public enum RewardType {

    AND {
        @Override
        public AndReward create() {
            return new AndReward();
        }
    },
    // 物品
    ITEM {
        @Override
        public ItemReward create() {
            return new ItemReward();
        }
    },


    ;

    public abstract <T extends AbstractReward> T create();

}
