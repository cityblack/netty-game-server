package com.lzh.game.start.model.util.condition;

import com.lzh.game.start.model.util.condition.handle.AndCondition;

public enum ConditionType {

    AND {
        @Override
        public AndCondition create() {
            return new AndCondition();
        }
    },
    ;


    public abstract <T extends AbstractCondition>T create();
}
