package com.lzh.game.start.model.compose.service;

import com.lzh.game.start.cmd.CmdMessage;
import com.lzh.game.start.model.compose.resource.ComposeResource;
import com.lzh.game.start.log.LogReason;
import com.lzh.game.start.model.player.Player;
import com.lzh.game.start.util.PacketUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ComposeService {

    @Autowired
    private ComposeResourceManage resourceManage;

    public void compose(Player player, int id) {
        ComposeResource resource = resourceManage.findComposeResourceById(id);
        checkCompose(player, resource);
        doCompose(player, resource);
    }

    private void checkCompose(Player player, ComposeResource resource) {
        resource.getConditionCache().verifyThrow(player);
        resource.getConsumeCache().verifyThrow(player);
        resource.getRewardCache().verifyThrow(player);
    }

    private void doCompose(Player player, ComposeResource resource) {
        resource.getConsumeCache().consume(player, LogReason.COMPOSE);
        resource.getRewardCache().reward(player, LogReason.COMPOSE);
    }
}
