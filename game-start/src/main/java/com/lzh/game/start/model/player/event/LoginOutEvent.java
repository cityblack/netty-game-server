package com.lzh.game.start.model.player.event;

import com.lzh.game.common.event.Event;
import com.lzh.game.start.model.player.Player;

public class LoginOutEvent implements Event {

    private Player player;

    public Object getOwn() {
        return player;
    }

    public static LoginOutEvent of(Player player) {
        LoginOutEvent event = new LoginOutEvent();
        event.player = player;
        return event;
    }
}
