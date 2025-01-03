package com.lzh.game.start.model.util.condition;

import com.lzh.game.start.model.util.condition.handle.AndCondition;

import java.util.Objects;
import java.util.stream.Stream;

public class ConditionUtils {

    public static AndCondition createEmptyAndConsume() {
        return ConditionType.AND.create();
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
