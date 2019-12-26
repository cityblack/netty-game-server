package com.lzh.game.start.model.player.model;

import com.lzh.game.common.Forward;
import com.lzh.game.repository.db.PersistEntity;
import com.lzh.game.start.util.TimeUtils;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;


@Data
@Document
public class PlayerEnt extends PersistEntity<Long> implements Serializable {

    private static final long serialVersionUID = 2728415253086770853L;

    @Id
    private long id;

    private String name;

    private String account;

    private int pid;

    private long createTime;

    private long logoutTime;

    private long loginTime;

    private boolean firstLogin;

    // 当前所在地图id
    private int mapId;
    // 当前所在地图的序号
    private int channel;
    // 当前位置x
    private int x;
    // 当前位置y
    private int y;
    // 当前等级
    private int level;
    // 当前经验
    private long exp;
    /**
     * 当前前进方向
     * {@link com.lzh.game.common.Forward}
     */
    private String forward;

    @Override
    public Long cacheKey() {
        return id;
    }

    public static PlayerEnt of(String account, String name) {
        PlayerEnt ent = new PlayerEnt();
        ent.setName(name);
        ent.setAccount(account);
        ent.setFirstLogin(true);
        ent.setLogoutTime(TimeUtils.now());
        ent.setCreateTime(TimeUtils.now());
        ent.setForward(Forward.RIGHT.name());
        return ent;
    }
}
