package com.lzh.game.start.model.player.action;

import com.lzh.game.socket.annotation.Action;
import com.lzh.game.socket.annotation.RequestMapping;
import com.lzh.game.socket.annotation.ResponseMapping;
import com.lzh.game.socket.exchange.session.Session;
import com.lzh.game.start.cmd.CmdMessage;
import com.lzh.game.start.model.player.packet.LoginRequest;
import com.lzh.game.start.model.player.packet.LoginResponse;
import com.lzh.game.start.model.player.packet.RegisterRequest;
import com.lzh.game.start.model.player.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;

@Action
public class PlayerAction {

    @Autowired
    private PlayerService playerService;

    @RequestMapping(CmdMessage.CM_LOGIN)
    @ResponseMapping(CmdMessage.SM_LOGIN)
    public LoginResponse login(Session session, LoginRequest loginRequest) {
        return playerService.login(loginRequest.getSign(), session);
    }

    @RequestMapping(CmdMessage.CM_REGISTER)
    public void register(Session session, RegisterRequest registerRequest) {
        playerService.register(session, registerRequest);
    }
}
