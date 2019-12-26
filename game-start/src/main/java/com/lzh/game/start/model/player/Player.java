package com.lzh.game.start.model.player;

import com.lzh.game.common.Forward;
import com.lzh.game.start.model.game.GameObjectType;
import com.lzh.game.start.model.game.SceneObject;
import com.lzh.game.start.model.item.bag.Bag;
import com.lzh.game.start.model.player.model.PlayerEnt;
import com.lzh.game.start.util.TimeUtils;
import com.lzh.game.start.model.wallet.Wallet;
import com.lzh.game.start.util.SpringContext;
import com.lzh.game.start.model.world.scene.Scene;
import com.lzh.game.start.model.world.scene.SceneKey;

import java.util.Objects;

/**
 * 不要在任何地方缓存Player对象!! Player存储在redis当中，获取的时候反序列化PlayerEnt获得
 * 为了减少序列化大小，所有字段不会主动赋值，所以获取不同的模块数据都需要延迟加载
 * 与玩家相关的模块数据 都需要从该类中延迟获取 防止数据不一致
 */
public class Player extends SceneObject {

    private Wallet wallet;

    private Bag bag;

    // =====
    private PlayerEnt playerEnt;

    public long getObjectId() {
        return playerEnt.getId();
    }

    @Override
    public GameObjectType objectType() {
        return GameObjectType.PLAYER;
    }

    public PlayerEnt getPlayerEnt() {
        return playerEnt;
    }

    public static Player of(PlayerEnt ent) {
        if (Objects.isNull(ent.getForward())) {
            ent.setForward(Forward.RIGHT.name());
        }
        Player player = new Player();
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
            wallet = context().getWalletService().getWalletById(getObjectId());
        }
        return wallet;
    }

    public boolean isFirstLogin() {
        return playerEnt.isFirstLogin();
    }

    /**
     * 当前所在场景
     * @return
     */
    public Scene currentScene() {
        return context().getSceneManage().getScene(getSceneKey());
    }

    public Bag getBag() {
        if (Objects.isNull(bag)) {
            bag = context().getBagDataManage().findBagByPlayer(this);
        }
        return bag;
    }

    @Override
    public SceneKey getSceneKey() {
        return SceneKey.of(playerEnt.getMapId(), playerEnt.getChannel());
    }

    public int getLevel() {
        return playerEnt.getLevel();
    }

    private SpringContext context() {
        return SpringContext.singleTon();
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

}
