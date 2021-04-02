package com.lzh.game.start.model.compose.resource;

import com.lzh.game.start.model.core.util.condition.Condition;
import com.lzh.game.start.model.core.util.condition.ConditionDef;
import com.lzh.game.start.model.core.util.condition.ConditionUtils;
import com.lzh.game.start.model.core.util.consume.Consume;
import com.lzh.game.start.model.core.util.consume.ConsumeDef;
import com.lzh.game.start.model.core.util.consume.ConsumeUtils;
import com.lzh.game.start.model.core.util.reward.Reward;
import com.lzh.game.start.model.core.util.reward.RewardDef;
import com.lzh.game.start.model.core.util.reward.RewardUtils;
import com.lzh.game.resource.Id;
import com.lzh.game.resource.Resource;
import com.lzh.game.resource.ResourceLoaded;
import lombok.Data;

@Data
@Resource
public class ComposeResource implements ResourceLoaded {

    @Id
    private int key;

    private ConditionDef[] condition;

    private ConsumeDef[] consume;

    private RewardDef[] reward;

    private Condition conditionCache;

    private Reward rewardCache;

    private Consume consumeCache;

    public Condition getConditionCache() {
        return conditionCache;
    }

    public Reward getRewardCache() {
        return rewardCache;
    }

    public Consume getConsumeCache() {
        return consumeCache;
    }

    @Override
    public void loaded() {
        conditionCache = ConditionUtils.createConditionByDef(condition);
        rewardCache = RewardUtils.createRewardByDef(reward);
        consumeCache = ConsumeUtils.createConsumeByDef(consume);
    }
}
