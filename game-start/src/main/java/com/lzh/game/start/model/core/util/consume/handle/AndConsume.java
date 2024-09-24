package com.lzh.game.start.model.core.util.consume.handle;


import com.lzh.game.start.log.LogReason;
import com.lzh.game.start.model.core.util.consume.AbstractConsume;
import com.lzh.game.start.model.player.Player;
import com.lzh.game.start.model.core.util.VerifyResult;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AndConsume extends AbstractConsume implements Iterable<AbstractConsume> {

    private List<AbstractConsume> consumes = new ArrayList<>();

    @Override
    protected void doParse(String value) {
        throw new IllegalArgumentException();
    }

    @Override
    public void doVerify(Object player, VerifyResult result, int multiple) {
        if (!consumes.isEmpty()) {
            for (int i = 0; i < consumes.size(); i++) {
                AbstractConsume consume = consumes.get(i);
                if (i == consumes.size() - 1) {
                    result.setLastContext(true);
                }
                consume.doVerify(player, result, multiple);
                if (!result.isSuccess()) {
                    return;
                }
            }
        }
    }

    public AndConsume addConsume(AbstractConsume consume) {
        if (consume instanceof AndConsume) {
            AndConsume and = (AndConsume)consume;
            for (AbstractConsume inner: and) {
                addConsume(inner);
            }
        } else {
            this.consumes.add(consume);
        }

        return this;
    }

    @Override
    public Iterator<AbstractConsume> iterator() {
        return consumes.iterator();
    }

    @Override
    public void consume(Object player, int multiple, LogReason logReason) {
        if (!consumes.isEmpty()) {
            for (AbstractConsume consume: consumes) {
                consume.consume(player, multiple, logReason);
            }
        }
    }
}
