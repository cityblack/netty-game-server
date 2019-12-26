package com.lzh.game.start.model.player.event;

import com.lzh.game.common.event.Event;
import com.lzh.game.start.model.player.Player;
import lombok.Data;

@Data
public class UpgradeLevelEvent implements Event {

    private transient Player player;

    private int sourceLevel;

    private int afterLevel;

    @Override
    public Player getOwn() {
        return player;
    }

    public static UpgradeLevelEvent of(Player player, int sourceLevel, int afterLevel) {
        UpgradeLevelEvent event = new UpgradeLevelEvent();
        event.player = player;
        event.sourceLevel = sourceLevel;
        event.afterLevel = afterLevel;
        return event;
    }
}
