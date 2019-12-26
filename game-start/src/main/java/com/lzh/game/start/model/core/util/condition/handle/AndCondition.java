package com.lzh.game.start.model.core.util.condition.handle;

import com.lzh.game.start.model.core.util.VerifyResult;
import com.lzh.game.start.model.core.util.condition.AbstractCondition;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AndCondition<T> extends AbstractCondition<T> implements Iterable<AbstractCondition> {

    private List<AbstractCondition> list = new ArrayList<>();

    @Override
    public void doVerify(T param, VerifyResult result) {
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

    public AndCondition addCondition(AbstractCondition condition) {
        if (condition instanceof AndCondition) {
            addCondition(condition);
        } else {
            list.add(condition);
        }

        return this;
    }

    @Override
    public Iterator<AbstractCondition> iterator() {
        return list.iterator();
    }
}
