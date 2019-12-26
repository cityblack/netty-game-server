package com.lzh.game.start.model.player.service.impl;

import com.lzh.game.resource.Static;
import com.lzh.game.resource.Storage;
import com.lzh.game.start.model.player.resource.PlayerLevelResource;
import org.springframework.stereotype.Component;

@Component
public class PlayerResourceManage {

    @Static
    private Storage<Integer, PlayerLevelResource> levelResourceStorage;

    public PlayerLevelResource findLevelResourceByLevel(int level) {
        return levelResourceStorage.getUnique(PlayerLevelResource.LEVEL_INDEX, level);
    }
}
