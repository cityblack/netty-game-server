package com.lzh.game.start.model.player.service.impl;

import com.lzh.game.common.util.IdGenerator;
import com.lzh.game.common.util.jwt.JwtTokenUtil;
import com.lzh.game.socket.exchange.session.Session;
import com.lzh.game.socket.exchange.session.manage.GameSessionManage;
import com.lzh.game.start.StartProperties;
import com.lzh.game.start.cmd.CmdMessage;
import com.lzh.game.start.log.LogFile;
import com.lzh.game.start.log.LogReason;
import com.lzh.game.start.log.LoggerUtils;
import com.lzh.game.start.model.i18n.I18n;
import com.lzh.game.start.model.i18n.RequestException;
import com.lzh.game.start.model.player.Player;
import com.lzh.game.start.model.player.model.PlayerEnt;
import com.lzh.game.start.model.player.packet.LoginResponse;
import com.lzh.game.start.model.player.packet.RegisterRequest;
import com.lzh.game.start.model.player.packet.RegisterResult;
import com.lzh.game.start.model.player.service.PlayerManage;
import com.lzh.game.start.model.player.service.PlayerService;
import com.lzh.game.start.model.player.service.PlayerSessionManage;
import com.lzh.game.start.util.PacketUtils;
import com.lzh.game.start.util.lock.LockUtils;
import com.lzh.game.start.model.world.Position;
import com.lzh.game.start.model.world.scene.Scene;
import com.lzh.game.start.model.world.scene.SceneKey;
import com.lzh.game.start.model.world.scene.SceneManage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.locks.Lock;

@Slf4j
@Component
public class PlayerServiceImpl implements PlayerService {

    @Autowired
    private PlayerManage playerManage;

    @Autowired
    private GameSessionManage sessionManage;

    @Autowired
    private PlayerSessionManage playerSessionManage;

    @Autowired
    private StartProperties properties;

    @Autowired
    private LockUtils lockUtils;

    @Override
    public LoginResponse login(String sign, Session session) {

        JwtTokenUtil.ValidateResult result = JwtTokenUtil.validateToken(sign, String.valueOf(properties.getGameId()));
        if (!result.isValid()) {
            return LoginResponse.of(false).setI18n(I18n.LOGIN_ERROR);
        }

        String id = result.getClaims().getId();
        if (Objects.isNull(id)) {
            log.error("非法登陆, jwt不存在Id:{}", sign);
            return LoginResponse.of(false).setI18n(I18n.LOGIN_ERROR);
        }
        Lock lock = lockUtils.getLock(id, Player.class);
        lock.lock();

        try {
            Player player = playerManage.loadPlayer(Long.valueOf(id));
            return login(player, session);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void logout(Player player) {
        doLogout(player, playerSessionManage.findSessionByPlayer(player));
    }

    private void doLogout(Player player, Session session) {
        if (Objects.nonNull(player)) {
            player.updateLogoutTime();
        }
        if (Objects.nonNull(session)) {
            playerSessionManage.removePlayerSession(session);
            if (log.isDebugEnabled()) {
                log.debug("Remove player:{} session:{}", player.getObjectId(), session.getId());
            }
        }
    }

    private LoginResponse login(Player player, Session session) {

        if (Objects.isNull(player)) {
            log.error("非法登陆, 角色不存在 session:{}", session);
            return LoginResponse.of(false).setI18n(I18n.LOGIN_ERROR);
        }
        player.updateLoginTime();
        playerSessionManage.bindSession(player, session);
        LoggerUtils
                .of(LogFile.LOGIN, LogReason.LOGIN)
                .log(player);
        playerManage.updatePlayer(player);
        return LoginResponse.of(true);
    }


    @Override
    public Player getPlayer(Session session) {
        if (!session.opened()) {
            throw new RuntimeException("session已经关闭");
        }
        Long playerId = playerSessionManage.findPlayerIdBySession(session);
        if (Objects.isNull(playerId)) {
            throw new RuntimeException("session已经关闭");
        }
        return getPlayerById(playerId);
    }

    @Override
    public void init() {
        // 注册session关闭事件

        sessionManage.addSessionCloseListening(session -> {
            Long playerId = playerSessionManage.findPlayerIdBySession(session);
            if (Objects.nonNull(playerId)) {
                doLogout(getPlayerById(playerId), session);
            } else {
                playerSessionManage.removePlayerSession(session);
            }
        });
    }

    @Override
    public Player getPlayerById(long playerId) {
        return playerManage.findPlayer(playerId);
    }

    @Override
    public void register(Session session, RegisterRequest registerRequest) {
        String account = registerRequest.getAccount();
        Lock lock = lockUtils.getLock(account);
        lock.lock();
        try {
            if (playerManage.existAccount(account)) {
                PacketUtils.send(session, CmdMessage.SM_REGISTER, RegisterResult.of(false));
            }
            PlayerEnt ent = PlayerEnt.of(account, registerRequest.getName());
            ent.setId(IdGenerator.singleton().createLongId());
            Player player = playerManage.addNewPlayer(ent);
            PacketUtils.send(session, CmdMessage.SM_REGISTER, RegisterResult.of(true));
            login(player, session);
        } finally {
            lock.unlock();
        }

    }

}
