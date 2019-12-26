package com.lzh.game.start.model.player.event;

import com.lzh.game.common.event.Event;
import com.lzh.game.start.model.player.Player;

public class AfterLoginEvent implements Event {

    private transient Player player;

    public Player getOwn() {
        return player;
    }

    public static AfterLoginEvent of(Player player) {
        AfterLoginEvent login = new AfterLoginEvent();
        login.player = player;
        return login;
    }
}
