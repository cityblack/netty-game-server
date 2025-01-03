package com.lzh.game.start.model.util.condition.handle;

import com.lzh.game.start.model.util.VerifyResult;
import com.lzh.game.start.model.util.condition.AbstractCondition;
import com.lzh.game.start.model.player.Player;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AndCondition extends AbstractCondition implements Iterable<AbstractCondition> {

    private final List<AbstractCondition> list = new ArrayList<>();

    @Override
    public void doVerify(Object param, VerifyResult result) {
        if (!list.isEmpty()) {
            for (int i = 0; i < list.size(); i++) {
                AbstractCondition condition = list.get(i);
                if (i == list.size() - 1) {
                    result.setLastContext(true);
                }
                condition.doVerify(param, result);
                if (!result.isSuccess()) {
                    return;
                }
            }
        }
    }

    @Override
    public void doParse(String value) {
        throw new IllegalArgumentException();
    }

    public void addCondition(AbstractCondition condition) {
        if (condition instanceof AndCondition and) {
            for (AbstractCondition inner: and) {
                addCondition(inner);
            }
        } else {
            list.add(condition);
        }
    }

    @Override
    public Iterator<AbstractCondition> iterator() {
        return list.iterator();
    }
}
