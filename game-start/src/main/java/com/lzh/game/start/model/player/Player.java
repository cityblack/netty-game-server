package com.lzh.game.start.model.player;

import com.lzh.game.framework.utils.TimeUtils;
import com.lzh.game.framework.repository.element.LongBaseEntity;
import com.lzh.game.start.util.ApplicationUtils;
import com.lzh.game.start.model.item.bag.Bag;
import com.lzh.game.start.model.item.bag.dao.BagDataManage;
import com.lzh.game.start.model.player.model.PlayerEnt;
import com.lzh.game.start.model.wallet.Wallet;

/**
 * Don't cache player data anywhere.
 */
public class Player extends LongBaseEntity {

    private long objectId;

    // =====
    private PlayerEnt playerEnt;

    public Wallet getWallet() {
        return playerEnt.getWallet();
    }

    public PlayerEnt getPlayerEnt() {
        return playerEnt;
    }

    public static Player of(PlayerEnt ent) {
        Player player = new Player();
        player.objectId = ent.getId();
        player.playerEnt = ent;
        return player;
    }


    public int currentMap() {
        return playerEnt.getMapId();
    }

    public int currentChannel() {
        return playerEnt.getChannel();
    }

    public boolean isFirstLogin() {
        return playerEnt.isFirstLogin();
    }

    public Bag getBag() {
        return ApplicationUtils.getBean(BagDataManage.class).findBagByPlayer(this);
    }

    public int getLevel() {
        return playerEnt.getLevel();
    }

    // ===
    public void updateLogoutTime() {
        this.playerEnt.setLogoutTime(TimeUtils.now());
    }

    public void updateLoginTime() {
        this.playerEnt.setLoginTime(TimeUtils.now());
    }

    public void setExp(long exp) {
        this.playerEnt.setExp(exp);
    }

    public void setLevel(int level) {
        this.playerEnt.setLevel(level);
    }

    public long getExp() {
        return playerEnt.getExp();
    }

    @Override
    public Long getKey() {
        return objectId;
    }
}
