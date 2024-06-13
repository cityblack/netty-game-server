package com.lzh.game.start.model.item.resource;

import com.lzh.game.start.model.core.util.condition.Condition;
import com.lzh.game.start.model.core.util.condition.ConditionDef;
import com.lzh.game.start.model.core.util.condition.ConditionUtils;
import com.lzh.game.start.model.core.util.consume.Consume;
import com.lzh.game.start.model.core.util.consume.ConsumeDef;
import com.lzh.game.start.model.core.util.consume.ConsumeUtils;
import com.lzh.game.start.model.core.util.reward.Reward;
import com.lzh.game.start.model.core.util.reward.RewardDef;
import com.lzh.game.start.model.core.util.reward.RewardUtils;
import com.lzh.game.framework.resource.Id;
import com.lzh.game.framework.resource.Resource;
import com.lzh.game.start.model.item.model.ItemType;
import lombok.Data;

import java.util.Objects;

@Data
@Resource
public class ItemResource {

    @Id
    private int key;

    private String name;
    // 可以堆叠的数量
    private short stack;

    private ItemType type;

    private RewardDef[] reward;

    private ConditionDef[] condition;

    private ConsumeDef[] consume;

    private Reward rewardCache;

    private Consume consumeCache;

    private Condition conditionCache;

    public Reward getReward() {
        if (Objects.isNull(rewardCache)) {
            rewardCache = Objects.isNull(reward) ? RewardUtils.createEmptyReward() : RewardUtils.createRewardByDef(reward);
        }
        return rewardCache;
    }

    public Consume getConsume() {
        if (Objects.isNull(consumeCache)) {
            consumeCache = Objects.isNull(consume) ? ConsumeUtils.createEmptyAndConsume() : ConsumeUtils.createConsumeByDef(consume);
        }
        return consumeCache;
    }

    public Condition getCondition() {
        if (Objects.isNull(conditionCache)) {
            conditionCache = Objects.isNull(condition) ? ConditionUtils.createEmptyAndConsume() : ConditionUtils.createConditionByDef(condition);
        }
        return conditionCache;
    }

    public boolean canStack() {
        return stack > 1;
    }
}
