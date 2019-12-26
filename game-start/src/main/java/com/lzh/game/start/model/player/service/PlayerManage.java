package com.lzh.game.start.model.player.service;

import com.lzh.game.start.model.player.Player;
import com.lzh.game.start.model.player.model.PlayerEnt;

public interface PlayerManage {

    Player findPlayer(Long playerId);

    void updatePlayer(Player player);

    Player loadPlayer(Long playerId);

    boolean existAccount(String account);

    Player addNewPlayer(PlayerEnt ent);
}
