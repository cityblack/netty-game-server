package com.lzh.game.start.model.compose.action;

import com.lzh.game.socket.annotation.Action;
import com.lzh.game.socket.annotation.RequestMapping;
import com.lzh.game.start.cmd.CmdMessage;
import com.lzh.game.start.model.compose.packet.ComposeRequest;
import com.lzh.game.start.model.compose.service.ComposeService;
import com.lzh.game.start.model.player.Player;
import org.springframework.beans.factory.annotation.Autowired;

@Action
public class ComposeAction {

    @Autowired
    private ComposeService composeService;

}
