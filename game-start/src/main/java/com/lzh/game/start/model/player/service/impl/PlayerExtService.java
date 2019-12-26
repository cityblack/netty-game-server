package com.lzh.game.start.model.player.service.impl;

import com.lzh.game.common.event.EventBus;
import com.lzh.game.start.log.LogReason;
import com.lzh.game.start.model.player.Player;
import com.lzh.game.start.model.player.event.UpgradeLevelEvent;
import com.lzh.game.start.model.player.log.PlayerLog;
import com.lzh.game.start.model.player.resource.PlayerLevelResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class PlayerExtService {

    @Autowired
    private PlayerResourceManage resourceManage;

    @Autowired
    private EventBus eventBus;

    public void addExp(Player player, long exp, LogReason logReason) {
        int level = player.getLevel();
        long sourceExp = player.getExp();
        long sumExp = sourceExp + exp;
        PlayerLevelResource resource = resourceManage.findLevelResourceByLevel(level);

        while (sumExp >= resource.getExp()) {
            int nextLevel = player.getLevel() + 1;
            PlayerLevelResource next = resourceManage.findLevelResourceByLevel(nextLevel);
            // 表示当前是满级
            if (Objects.isNull(next)) {
                sumExp = resource.getExp();
                break;
            } else {
                player.setLevel(nextLevel);
                sumExp -= resource.getExp();
                eventBus.globalPost(UpgradeLevelEvent.of(player, nextLevel - 1, nextLevel));
            }
        }
        player.setExp(sumExp);
        PlayerLog.addExp(player, level, player.getLevel(), sourceExp, player.getExp(), exp, logReason);
    }
}
