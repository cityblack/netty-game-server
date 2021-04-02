package com.lzh.game.start.model.player;

import com.lzh.game.common.ApplicationUtils;
import com.lzh.game.common.Forward;
import com.lzh.game.common.util.TimeUtils;
import com.lzh.game.repository.BaseEntity;
import com.lzh.game.start.model.item.bag.Bag;
import com.lzh.game.start.model.item.bag.dao.BagDataManage;
import com.lzh.game.start.model.player.model.PlayerEnt;
import com.lzh.game.start.model.wallet.Wallet;
import com.lzh.game.start.model.wallet.service.WalletService;

import java.util.Objects;

/**
 * Don't cache player data anywhere.
 */
public class Player extends BaseEntity<Long> {

    private long objectId;

    private Wallet wallet;

    private Bag bag;

    // =====
    private PlayerEnt playerEnt;

    public PlayerEnt getPlayerEnt() {
        return playerEnt;
    }

    public static Player of(PlayerEnt ent) {
        if (Objects.isNull(ent.getForward())) {
            ent.setForward(Forward.RIGHT.name());
        }
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

    public Wallet getWallet() {
        if (Objects.isNull(wallet)) {
            wallet = ApplicationUtils.getBean(WalletService.class).getWalletById(getKey());
        }
        return wallet;
    }

    public boolean isFirstLogin() {
        return playerEnt.isFirstLogin();
    }


    public Bag getBag() {
        if (Objects.isNull(bag)) {
            bag = ApplicationUtils.getBean(BagDataManage.class).findBagByPlayer(this);
        }
        return bag;
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
