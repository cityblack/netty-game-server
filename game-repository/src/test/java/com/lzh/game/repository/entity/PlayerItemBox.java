package com.lzh.game.repository.entity;

import com.lzh.game.common.GridTable;
import com.lzh.game.repository.db.PersistEntity;
import com.lzh.game.repository.model.AbstractItem;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Document
public class PlayerItemBox extends PersistEntity<String> implements Serializable {

    @Id
    private String player;

    private GridTable<AbstractItem> items = new GridTable<>();

    private long updateTime;

    @Override
    public String cacheKey() {
        return player;
    }

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public GridTable<AbstractItem> getItems() {
        return items;
    }

    public void setItems(GridTable<AbstractItem> items) {
        this.items = items;
    }


    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }
}
