package com.lzh.game.start;

import com.lzh.game.start.model.player.Player;
import com.lzh.game.start.model.player.model.PlayerEnt;
import com.lzh.game.start.util.SpringContext;

import java.util.Objects;

public class PlayerUtils {

    public static Player create(long id) {
        PlayerEnt ent = PlayerEnt.of("10086", "测试");
        ent.setId(id);
        return SpringContext.singleTon().getPlayerManage().addNewPlayer(ent);
    }

    public static Player get() {
        long id = 10086;
        Player player = SpringContext.singleTon().getPlayerManage().loadPlayer(id);
        if (Objects.nonNull(player)) {
            return player;
        }
        return create(id);
    }
}
