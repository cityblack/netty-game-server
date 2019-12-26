package com.lzh.game.start.model.player.service;

import com.lzh.game.socket.exchange.session.Session;
import com.lzh.game.start.model.player.Player;
import com.lzh.game.start.model.player.packet.LoginResponse;
import com.lzh.game.start.model.player.packet.RegisterRequest;

public interface PlayerService {

    Player getPlayerById(long playerId);

    /**
     * 登陆接口
     * 登陆与游戏服分离， 先在登陆服登陆完成后， 将返回的token带回验证
     * @param sign -- Json web token
     * @param session
     * @return
     */
    LoginResponse login(String sign, Session session);

    void logout(Player player);

    Player getPlayer(Session session);

    void init();

    void register(Session session, RegisterRequest registerRequest);
}
