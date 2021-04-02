package com.lzh.game.start.model.core.util.condition;

import com.lzh.game.start.model.core.util.condition.handle.AndCondition;

import java.util.Objects;
import java.util.stream.Stream;

public class ConditionUtils {

    public static AndCondition createEmptyAndConsume() {
        AndCondition handle = ConditionType.AND.create();
        return handle;
    }

    public static Condition createConditionByDef(ConditionDef[] def) {
        Objects.requireNonNull(def);
        AndCondition and = createEmptyAndConsume();
        Stream.of(def)
                .map(e -> createCondition(e.getType(),e.getValue()))
                .forEach(and::addCondition);
        return and;
    }

    public static AbstractCondition createCondition(ConditionType type, String value) {
        Objects.requireNonNull(type);
        AbstractCondition condition = type.create();
        condition.parse(value);
        return condition;
    }
}
