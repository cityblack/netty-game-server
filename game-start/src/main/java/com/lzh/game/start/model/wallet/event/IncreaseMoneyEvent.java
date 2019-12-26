package com.lzh.game.start.model.wallet.event;

import com.lzh.game.common.event.Event;
import com.lzh.game.start.log.LogReason;
import com.lzh.game.start.model.currency.model.CurrencyType;
import com.lzh.game.start.model.player.Player;
import lombok.Getter;

public class IncreaseMoneyEvent implements Event {

    private Player player;
    @Getter
    private LogReason logReason;
    @Getter
    private long addValue;
    @Getter
    private long currentValueSum;
    @Getter
    private CurrencyType type;

    @Override
    public Player getOwn() {
        return player;
    }

    public static IncreaseMoneyEvent of(Player player, LogReason logReason, long addValue, long currentValueSum, CurrencyType type) {
        IncreaseMoneyEvent event = new IncreaseMoneyEvent();

        event.player = player;
        event.logReason = logReason;
        event.addValue = addValue;
        event.currentValueSum = currentValueSum;
        event.type = type;
        return event;
    }
}
