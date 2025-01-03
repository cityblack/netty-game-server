package com.lzh.game.start.model.util.reward;

import com.lzh.game.start.model.util.reward.handle.AndReward;

import java.util.Objects;
import java.util.stream.Stream;

public class RewardUtils {

    public static AndReward createEmptyReward() {
        return RewardType.AND.create();
    }

    public static AndReward createRewardByDef(RewardDef[] rewardDef) {
        Objects.requireNonNull(rewardDef);
        AndReward andReward = new AndReward();
        Stream.of(rewardDef)
                .map(RewardUtils::createRewardByDef)
                .forEach(andReward::addReward);
        return andReward;
    }

    public static AbstractReward createRewardByDef(RewardDef rewardDef) {
        return createReward(rewardDef.getType(), rewardDef.getValue());
    }

    public static AbstractReward createReward(RewardType type, String value) {
        AbstractReward reward = type.create();
        reward.doParse(value);
        return reward;
    }
}
