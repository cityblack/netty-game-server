package com.lzh.game.start.model.core.util.reward;

import com.lzh.game.start.model.core.util.reward.handle.*;

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

    CURRENCY {
        @Override
        public CurrencyReward create() {
            return new CurrencyReward();
        }
    }
    ;

    public abstract <T extends AbstractReward> T create();

}
