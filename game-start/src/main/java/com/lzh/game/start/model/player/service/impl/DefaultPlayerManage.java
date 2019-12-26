package com.lzh.game.start.model.player.service.impl;

import com.lzh.game.repository.CacheDataRepository;
import com.lzh.game.start.model.game.GameObject;
import com.lzh.game.start.model.game.GameObjectManage;
import com.lzh.game.start.model.game.GameObjectType;
import com.lzh.game.start.model.player.Player;
import com.lzh.game.start.model.player.dao.PlayerRepository;
import com.lzh.game.start.model.player.model.PlayerEnt;
import com.lzh.game.start.model.player.service.PlayerManage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Slf4j
@Component
public class DefaultPlayerManage implements PlayerManage, GameObjectManage, DisposableBean {

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private CacheDataRepository dataRepository;

    @Override
    public Player findPlayer(Long playerId) {
        return Player.of(dataRepository.get(playerId, PlayerEnt.class));
    }

    @Override
    public void updatePlayer(Player player) {
        dataRepository.update(player.getPlayerEnt());
    }

    @Override
    public Player loadPlayer(Long playerId) {
        PlayerEnt playerEnt = dataRepository.loadOrCreate(Long.valueOf(playerId), PlayerEnt.class, key -> playerRepository.findById(key).orElseGet(() -> null));
        if (Objects.isNull(playerEnt)) {
            return null;
        }
        return Player.of(playerEnt);
    }

    @Override
    public boolean existAccount(String account) {
        return playerRepository.existsByAccount(account);
    }

    @Override
    public Player addNewPlayer(PlayerEnt ent) {

        return Player.of(dataRepository.addAndSave(ent.getId(), ent));
    }

    @Override
    public void destroy() throws Exception {

    }

    @Override
    public GameObject findVisible(long objectId) {
        return Player.of(dataRepository.get(objectId, PlayerEnt.class));
    }

    @Override
    public GameObjectType type() {
        return GameObjectType.PLAYER;
    }
}
