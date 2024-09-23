package com.lzh.game.start.model.core.util.reward.handle;

import com.lzh.game.start.log.LogReason;
import com.lzh.game.start.model.core.util.VerifyResult;
import com.lzh.game.start.model.core.util.reward.AbstractReward;
import com.lzh.game.start.model.player.Player;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AndReward extends AbstractReward implements Iterable<AbstractReward> {

    private final List<AbstractReward> list = new ArrayList<>(8);

    @Override
    protected void doParse(String value) {
        throw new IllegalArgumentException();
    }

    @Override
    public void doVerify(Object param, VerifyResult result, int multiple) {
        if (!list.isEmpty()) {
            for (int i = 0; i < list.size(); i++) {
                AbstractReward reward = list.get(i);
                if (i == list.size() - 1) {
                    result.setLastContext(true);
                }
                reward.doVerify(param, result, multiple);
                if (!result.isSuccess()) {
                    return;
                }
            }
        }
    }

    @Override
    public void reward(Object player, int multiple, LogReason logReason) {
        if (!list.isEmpty()) {
            for (AbstractReward reward: list) {
                reward.reward(player, multiple, logReason);
            }
        }
    }

    @Override
    public Iterator<AbstractReward> iterator() {
        return list.iterator();
    }

    public void addReward(AbstractReward reward) {
        if (reward instanceof AndReward and) {
            for (AbstractReward inner: and) {
                addReward(inner);
            }
        } else {
            this.list.add(reward);
        }
    }
}
