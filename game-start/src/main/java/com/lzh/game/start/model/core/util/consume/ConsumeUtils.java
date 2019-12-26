package com.lzh.game.start.model.core.util.consume;

import com.lzh.game.start.model.core.util.consume.handle.AndConsume;
import com.lzh.game.start.model.core.util.consume.handle.ItemConsume;

import java.util.Objects;
import java.util.stream.Stream;

public final class ConsumeUtils {

    public static AbstractConsume createConsume(ConsumeType type, String value) {
        AbstractConsume consume = type.create();
        consume.parse(value);
        return consume;
    }

    //物品消耗创建
    public static AbstractConsume createConsumeByItem(int itemId, int multiple) {
        ItemConsume consume = ConsumeType.ITEM.create();
        consume.setItemId(itemId);
        consume.setNum(multiple);
        return consume;
    }

    public static AndConsume createEmptyAndConsume() {
        AndConsume consume = ConsumeType.AND.create();
        return consume;
    }

    public static AbstractConsume createConsumeByDef(ConsumeDef[] consumeDef) {

        AndConsume andConsume = createEmptyAndConsume();
        if (Objects.isNull(consumeDef)) {
            return andConsume;
        }
        Stream.of(consumeDef)
                .map(ConsumeUtils::createConsumeByDef)
                .forEach(andConsume::addConsume);
        return andConsume;
    }

    public static AbstractConsume createConsumeByDef(ConsumeDef def) {

        return createConsume(def.getType(), def.getValue());
    }
}
