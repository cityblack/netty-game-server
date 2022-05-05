package com.lzh.game.start.config;

import com.lzh.game.common.ApplicationUtils;
import com.lzh.game.socket.Request;
import com.lzh.game.socket.core.invoke.ParamConvert;
import com.lzh.game.socket.core.session.Session;
import com.lzh.game.start.model.player.Player;
import com.lzh.game.start.model.player.service.PlayerService;

public class PlayerConvert implements ParamConvert<Player> {

    @Override
    public Player convert(Request request) {
        Session session = request.getSession();
        return ApplicationUtils.getBean(PlayerService.class).getPlayer(session);
    }
}
